package main

import (
	"crypto/tls"
	"encoding/json"
	"log"
	"net/http"
	"strconv"
	"time"

	"github.com/sajari/regression"
)

/*
	Works as a "security" level
*/

const ( //0,1,...
	GeneralPublic = "PUBLIC"
	Student       = "STUDENT"
	Professor     = "PROFESSOR"
	Researcher    = "RESEARCHER"
	Staff         = "STAFF"
)

var SIZE = 10

//Rest API structs
type RegisterRequest struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	IST      string `json:"ist"`
	Password string `json:"password"`
	Level    string `json:"level"` //FIXME: verify field
}

type RegisterResponse struct {
	Status string `json:"status"`
}

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type LoginResponse struct {
	Status   string `json:"status"`
	Username string `json:"username"`
	Password string `json:"password"`
	Email    string `json:"email"`
	IST      string `json:"ist"`
	Level    string `json:"level"`
	Image    string `json:"image"`
}

type LogoutRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type LogoutResponse struct {
	Status string `json:"status"`
}

type AddMenuRequest struct {
	NameMenu    string `json:"namemenu"`
	NameCanteen string `json:"namecanteen"`
	Price       string `json:"price"`
	Username    string `json:"username"`
	Password    string `json:"password"`
	Dietary     string `json:"dietary"`
}

type AddMenuResponse struct {
	Status string `json:"status"`
}

type RateMenuRequest struct {
	NameMenu    string `json:"namemenu"`
	NameCanteen string `json:"namecanteen"`
	Username    string `json:"username"`
	Password    string `json:"password"`
	Rate        int    `json:"rate,string"`
}

type RateMenuResponse struct {
	Status    string  `json:"status"`
	Average   float64 `json:"average"`
	NumRating int     `json:"numrating"`
}

type GetRatesMenuRequest struct {
	NameMenu    string `json:"menu"`
	NameCanteen string `json:"canteen"`
	Username    string `json:"username"`
	Password    string `json:"password"`
}

type GetRatesMenuResponse struct {
	Ratings []int `json:"ratings"`
	Status string `json:"status"`
}

type GetRatesCanteenRequest struct {
	NameCanteen string `json:"canteen"`
	Username    string `json:"username"`
	Password    string `json:"password"`
}

type GetRatesCanteenResponse struct {
	Ratings []int `json:"ratings"`
	Status string `json:"status"`
}

type AddImageRequest struct {
	NameMenu    string `json:"namemenu"`
	NameCanteen string `json:"namecanteen"`
	NameImage   string `json:"nameimage"`
	Image       string `json:"image"`
	Username    string `json:"username"`
	Password    string `json:"password"`
	ProfilePicName string `json:"profilepic"`
}

type AddImageResponse struct {
	Status string `json:"status"`
}

type AddImageProfileRequest struct {
	Image    string `json:"image"`
	Username string `json:"username"`
	Password string `json:"password"`
}

type AddImageProfileResponse struct {
	Status string `json:"status"`
}

type GetMenusRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	Canteen  string `json:"canteen"`
}
type GetMenusResponse struct {
	Menus  []MenusInterface `json:"menus"`
	Status string           `json:"status"`
}

type GetCanteensRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	Campus   string `json:"campus"`
}
type GetCanteensResponse struct {
	Canteens []CanteenInterface `json:"canteens"`
	Status   string             `json:"status"`
}

// will only return the names of the images
type GetImageNamesRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	Canteen  string `json:"canteen"`
	Menu     string `json:"menu"`
	Page     int    `json:"page,string"`
}

type GetImageNamesResponse struct {
	Images []string `json:"images"`
	Status string   `json:"status"`
}

type GetBulkImagesRequest struct {
	Username string   `json:"username"`
	Password string   `json:"password"`
	Canteen  string   `json:"canteen"`
	Menu     string   `json:"menu"`
	Images   []string `json:"images"`
}

type GetBulkImagesResponse struct {
	Images []Image `json:"images"`
	Status string  `json:"status"`
}

type GetPreFetchImagesMenuRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	NrImages int    `json:"nrimages,string"`
}

type GetPreFetchImagesMenuResponse struct {
	Images []ImageMet `json:"images"`
	Status string     `json:"status"`
}

type QueueRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	Canteen  string `json:"canteen"`
	Minutes  string `json:"minutes"`
}
type QueueResponse struct {
	Status string `json:"status"`
	Queue  int    `json:"queue"`
}

type MenusInterface struct {
	Name    string  `json:"name"`
	Price   float64 `json:"price"`
	Dietary string  `json:"dietary"`
	Ratings float64 `json:"ratings"`
	ProfilePicName string `json:"profilepic"`
}

type CanteenInterface struct {
	Name       string `json:"name"`
	Prediction int    `json:"prediction"`
}

type ImageMet struct {
	Image   Image  `json:"image"`
	Canteen string `json:"canteen"`
	Menu    string `json:"menu"`
}

//Business Logic structs
type Coordinates struct {
	Lat float64 `json:"lat"`
	Lng float64 `json:"lng"`
}

type TimeInterval struct {
	Open  time.Time `json:"open"`
	Close time.Time `json:"close"`
}

type Position struct {
	Minutes int
	Number  int
}

type Canteen struct {
	Menus      map[string]*Menu
	Location   Coordinates
	OpenHours  map[string]TimeInterval
	Queue      []*User
	Campus     string
	Type       string
	Regression *regression.Regression
}

type Menu struct {
	Price   float64 //should work?
	Gallery []Image //slice of images
	Dietary string
	ProfilePicName string
	Ratings map[string]int //TODO: Update this part with more info (more detailed breakdown of user ratings (e.g.histogram))
}

type Image struct {
	Name  string `json:"name"`
	Image string `json:"image"`
}

type User struct {
	Email    string
	IST      string
	Name     string
	Password string
	Level    string
	LoggedIn bool
	InQueue  Position
	Image    string
}

//Global Variables
var users = make(map[string]*User) //Key is user and Value is password

var places = make(map[string]*Canteen) // Key is the place name and Value is its contents

//Aux Functions
func validadeUser(username, password string) (*User, string, int) {

	if len(username) == 0 {
		return nil, "Empty Username", http.StatusUnauthorized
	}

	if len(password) == 0 {
		return nil, "Empty Password", http.StatusUnauthorized
	}

	user, ok := users[username]
	if !ok {
		return nil, "User does not exist", http.StatusUnauthorized
	}

	if user.Password != password {
		return nil, "Wrong Password", http.StatusUnauthorized
	}

	if ok && !user.LoggedIn {
		return user, "Not Logged in", http.StatusUnauthorized
	}

	if ok && user.LoggedIn && user.Password == password {
		return user, "OK", http.StatusOK
	}

	return nil, "Unexpected error", http.StatusBadRequest
}
func validadeCanteen(canteenName string) (*Canteen, string, int) {

	if len(canteenName) == 0 {
		return nil, "Empty Canteen Name", http.StatusBadRequest
	}

	canteen, ok := places[canteenName]
	if !ok {
		return nil, "Canteen does not exist", http.StatusBadRequest
	}

	return canteen, "OK", http.StatusOK

}

//Handlers
func registerHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest RegisterRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New register  received")
	log.Println(userRequest)

	//test if the user already exists
	_, ok := users[userRequest.Username]
	if ok {
		log.Println("[ERROR] User already created")
		http.Error(w, "User already created", http.StatusBadRequest)
		return
	}

	if len(userRequest.Username) == 0 {
		log.Println("[ERROR] Empty Username")
		http.Error(w, "Empty Username", http.StatusBadRequest)
		return
	}

	if len(userRequest.Password) == 0 {
		log.Println("[ERROR] Empty Password")
		http.Error(w, "Empty Password", http.StatusBadRequest)
		return
	}

	users[userRequest.Username] = &User{Password: userRequest.Password,
		Email:    userRequest.Email,
		Name:     userRequest.Username,
		IST:      userRequest.IST,
		Level:    userRequest.Level,
		LoggedIn: false,
		InQueue:  Position{}}
	//FIXME: This is probably not needed
	response := RegisterResponse{
		Status: "OK"}

	json.NewEncoder(w).Encode(response)
}

//TODO: This way the app has to also logout, maybe not needed?
func loginHandler(w http.ResponseWriter, r *http.Request) {

	var userRequest LoginRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New login  received")
	log.Println(userRequest)

	//test if the user exists
	user, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if user == nil {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	user.LoggedIn = true
	response := LoginResponse{
		Status:   "OK",
		Username: user.Name,
		Email:    user.Email,
		IST:      user.IST,
		Password: user.Password,
		Level:    user.Level,
		Image:    user.Image,
	}

	json.NewEncoder(w).Encode(response)
}

func logoutHandler(w http.ResponseWriter, r *http.Request) {

	var userRequest LogoutRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New logout  received")
	log.Println(userRequest)

	//test if the user exists
	user, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println(user)
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	user.LoggedIn = false

	response := LogoutResponse{
		Status: "OK"}

	json.NewEncoder(w).Encode(response)
}

func addMenuHandler(w http.ResponseWriter, r *http.Request) {

	var userRequest AddMenuRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New menu received")
	log.Println(userRequest)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.NameCanteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	if s, err := strconv.ParseFloat(userRequest.Price, 64); err == nil {
		canteen.Menus[userRequest.NameMenu] = &Menu{Price: s, Dietary: userRequest.Dietary, Ratings: make(map[string]int)}
	} else {
		log.Println("[ERROR] Bad Price value")
		http.Error(w, "Bad Price value", http.StatusBadRequest)
		return
	}

	response := AddMenuResponse{
		Status: "OK"}

	json.NewEncoder(w).Encode(response)
}

func addImageHandler(w http.ResponseWriter, r *http.Request) {

	var userRequest AddImageRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("Receiving image ...")
	//log.Println(userRequest)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.NameCanteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	//canteen.Menus[userRequest.NameMenu].Gallery = append(canteen.Menus[userRequest.NameMenu].Gallery, Image{Name: userRequest.NameImage, Image: userRequest.Image})
	canteen.Menus[userRequest.NameMenu].Gallery = append([]Image{Image{Name: userRequest.NameImage, Image: userRequest.Image}}, canteen.Menus[userRequest.NameMenu].Gallery...)
    canteen.Menus[userRequest.NameMenu].ProfilePicName = userRequest.ProfilePicName

	response := AddImageResponse{
		Status: "OK"}

	json.NewEncoder(w).Encode(response)
}

func addImageProfileHandler(w http.ResponseWriter, r *http.Request) {

	var userRequest AddImageProfileRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("Receiving profile image ...")
	//log.Println(userRequest)

	//test if the user already exists
	user, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	user.Image = userRequest.Image

	response := AddImageProfileResponse{
		Status: "OK"}

	json.NewEncoder(w).Encode(response)
}

func rateMenuHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest RateMenuRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New Rating Received")
	log.Println(userRequest)
	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.NameCanteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen.Menus[userRequest.NameMenu].Ratings[userRequest.Username] = userRequest.Rate
    log.Print("User ", userRequest.Username, " rating:", canteen.Menus[userRequest.NameMenu].Ratings[userRequest.Username])

	count := 0
	sum := 0
	for _, value := range canteen.Menus[userRequest.NameMenu].Ratings {
		count++
		sum += value
	}

	// count1 := 0
	// sum1 := 0
	// for _, menu := range canteen.Menus {
	// 	for _, value1 := menu.Ratings {
	// 		count1++
	// 		sum1 += value1
	// 	}
	// }

	response := RateMenuResponse{
		Status:    "OK",
		Average:   float64(sum) / float64(count),
		NumRating: count}

	json.NewEncoder(w).Encode(response)
}

func getMenuRatesHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest GetRatesMenuRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("Get Menu Ratings Request Received")
	log.Println(userRequest)
	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.NameCanteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	var ratings []int

	for _, value := range canteen.Menus[userRequest.NameMenu].Ratings {
    		ratings = append(ratings, value)
    }

	response := GetRatesMenuResponse{
		Status:    "OK",
		Ratings: ratings}

	json.NewEncoder(w).Encode(response)
}

func getCanteenRatesHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest GetRatesCanteenRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("Get Canteen Ratings Request Received")
	log.Println(userRequest)
	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.NameCanteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

    var ratings []int
    for _, menu := range canteen.Menus {
        for _, value := range menu.Ratings {
            ratings = append(ratings, value)
        }
    }

	response := GetRatesCanteenResponse{
		Status:    "OK",
		Ratings:   ratings}

	json.NewEncoder(w).Encode(response)
}


func getCanteensHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest GetCanteensRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New request for canteens Received")
	log.Println(userRequest)
	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	var canteens []CanteenInterface

	for key, value := range places {
		log.Println([]float64{float64(len(value.Queue))})
		prediction, err := value.Regression.Predict([]float64{float64(len(value.Queue))})

		if err == nil {
			prediction2 := int(prediction)
			log.Println(value.Campus)
			log.Println(prediction2)

			if value.Campus == userRequest.Campus {
				canteens = append(canteens, CanteenInterface{
					Name:       key,
					Prediction: prediction2})
			}

		} else {
			log.Println(err)
		}

	}
	response := GetCanteensResponse{
		Status:   "OK",
		Canteens: canteens}

	json.NewEncoder(w).Encode(response)
}

func getMenusHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest GetMenusRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New request for menus Received")
	log.Println(userRequest)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.Canteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	var menus []MenusInterface

	for key, value := range canteen.Menus {
		sum := 0
		count := 0
		for _, rating := range value.Ratings {
			sum += rating
			count++
		}
		if count == 0 {
			menus = append(menus, MenusInterface{
				Price:   value.Price,
				Name:    key,
				Dietary: value.Dietary,
				ProfilePicName: value.ProfilePicName,
				Ratings: 0})
		} else {
			menus = append(menus, MenusInterface{
				Price:   value.Price,
				Name:    key,
				Dietary: value.Dietary,
				ProfilePicName: value.ProfilePicName,
				Ratings: float64(sum) / float64(count)})
		}
		log.Println(value.ProfilePicName)

	}
	response := GetMenusResponse{
		Status: "OK",
		Menus:  menus}

	json.NewEncoder(w).Encode(response)
}

func getImagesHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest GetBulkImagesRequest
	//var userRequest GetTestRequest

	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New request for images Received")
	log.Println(userRequest)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.Canteen)
	//_, stmt, status = validadeCanteen(userRequest.Canteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}
	var matches []Image
	var response GetBulkImagesResponse
	log.Println("Sending images...")

	//FIXME: no need to iterate through the entire slice if page is provided
	for _, imageName := range userRequest.Images {
		for _, n := range canteen.Menus[userRequest.Menu].Gallery {
			if imageName == n.Name {
				matches = append(matches, n)
			}
		}
	}

	response = GetBulkImagesResponse{
		Status: "OK",
		Images: matches}

	json.NewEncoder(w).Encode(response)
}

func getImagesNameHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest GetImageNamesRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New request for imageNames Received")
	log.Println(userRequest)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.Canteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}
	begin := SIZE * userRequest.Page
	end := SIZE*userRequest.Page + SIZE
	var matches []string

	var response GetImageNamesResponse
	log.Println("Sending images...")

	for i := begin; i < end && i < len(canteen.Menus[userRequest.Menu].Gallery); i++ {
		if canteen.Menus[userRequest.Menu].Gallery[i].Name[0] == 'T' {
			matches = append(matches, canteen.Menus[userRequest.Menu].Gallery[i].Name)
			log.Print("MATCH: ")
		}
		log.Println(canteen.Menus[userRequest.Menu].Gallery[i].Name)
	}
	response = GetImageNamesResponse{
		Status: "OK",
		Images: matches}

	json.NewEncoder(w).Encode(response)
}

func prefetchMenuImages(w http.ResponseWriter, r *http.Request) {
	var userRequest GetPreFetchImagesMenuRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New request to prefetch menu Received")
	log.Println(userRequest)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	var images []ImageMet

	type Names struct {
		Canteen string
		Menus   []string
	}
	var tmp []Names
	var menunames []string
	var total int
	//create tmp struct to view the data differently
	//now its possible to jump around the canteens
	for canteenname, canteen := range places {
		for menuname, menu := range canteen.Menus {
			menunames = append(menunames, menuname)
			total += len(menu.Gallery)
		}
		tmp = append(tmp, Names{Canteen: canteenname, Menus: menunames})
	}
	log.Println(total)
	ctr := 0
	for canteenname, canteen := range places {
		for menuname, menu := range canteen.Menus {
			for _, image := range menu.Gallery {
				if ctr < userRequest.NrImages {
					images = append(images, ImageMet{Image: image, Canteen: canteenname, Menu: menuname})
				} else {
					break
				}
				ctr++
			}
			if ctr >= userRequest.NrImages {
				break
			}
		}
		if ctr >= userRequest.NrImages {
			break
		}
	}

	// for _, names := range tmp {
	// 	log.Println("[DEBUGF] ", names.Canteen)
	// 	for _, menu := range names.Menus {
	// 		if ctr >= userRequest.NrImages && len(places[names.Canteen].Menus) != 0 {
	// 			for _, image := range places[names.Canteen].Menus[menu].Gallery {
	// 				if ctr < userRequest.NrImages {
	// 					images = append(images, ImageMet{Image: image, Canteen: names.Canteen, Menu: menu})
	// 				} else {
	// 					break
	// 				}
	// 				ctr++
	// 			}
	// 		}
	// 	}

	// }

	response := GetPreFetchImagesMenuResponse{
		Status: "OK",
		Images: images}

	json.NewEncoder(w).Encode(response)
}

func queueHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest QueueRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	//test if the user already exists
	user, stmt, status := validadeUser(userRequest.Username, userRequest.Password)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.Canteen)
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}

	minutes, err := strconv.Atoi(userRequest.Minutes)
	if user.InQueue == (Position{}) { //if it isnt in a queue add it

		log.Println("New request to enter queue Received")
		log.Println(userRequest)

		if err != nil {
			log.Println("[ERROR] Couldnt convert string to int")
			http.Error(w, "Couldnt convert string to int", http.StatusBadRequest)
			return
		}

		user.InQueue = Position{Minutes: minutes, Number: len(canteen.Queue)}
		canteen.Queue = append(canteen.Queue, user)

	} else {

		log.Println("New request to leave queue Received")
		log.Println(userRequest)

		for i, n := range canteen.Queue {
			if user == n { //will only happen once

				canteen.Regression.Train(regression.DataPoint(float64(minutes-user.InQueue.Minutes), []float64{float64(user.InQueue.Number)}))
				canteen.Regression.Run()

				canteen.Queue = append(canteen.Queue[:i], canteen.Queue[i+1:]...)
				user.InQueue = Position{} //empty position
			}
		}
	}

	response := QueueResponse{
		Status: "OK",
		Queue:  user.InQueue.Number,
	}

	json.NewEncoder(w).Encode(response)
}

// INITIALIZATION FUNCTIONS

func addPlace(name string, typ string, coord Coordinates, times map[string]TimeInterval, campus string) {
	places[name] = &Canteen{Menus: make(map[string]*Menu),
		Type:       typ,
		Location:   coord,
		OpenHours:  times,
		Campus:     campus,
		Regression: new(regression.Regression)}
}

func initPlaces() { // initiate more if needed
	timeLayout := "15:04:05"
	//	MonicaOpenHours, _ := time.Parse(timeLayout, "08:00:00")
	//	MonicaCloseHours, _ := time.Parse(timeLayout, "17:00:00")
	//	AbilioOpenHours, _ := time.Parse(timeLayout, "10:00:00")
	//	AbilioCloseHours, _ := time.Parse(timeLayout, "20:00:00")

	seven_am, _ := time.Parse(timeLayout, "07:00:00")
	eight_am, _ := time.Parse(timeLayout, "08:00:00")
	eight_half_am, _ := time.Parse(timeLayout, "08:30:00")
	nine_am, _ := time.Parse(timeLayout, "09:00:00")
	twelve_am, _ := time.Parse(timeLayout, "12:00:00")
	one_half_pm, _ := time.Parse(timeLayout, "13:30:00")
	two_pm, _ := time.Parse(timeLayout, "14:00:00")
	three_pm, _ := time.Parse(timeLayout, "15:00:00")
	//three_half_pm,_ := time.Parse(timeLayout, "15:30:00")
	four_half_pm, _ := time.Parse(timeLayout, "15:30:00")
	five_pm, _ := time.Parse(timeLayout, "17:00:00")
	seven_pm, _ := time.Parse(timeLayout, "19:00:00")
	nine_pm, _ := time.Parse(timeLayout, "21:00:00")
	ten_pm, _ := time.Parse(timeLayout, "22:00:00")

	nine_to_five := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: nine_am, Close: five_pm},
		Student:       TimeInterval{Open: nine_am, Close: five_pm},
		Professor:     TimeInterval{Open: nine_am, Close: five_pm},
		Researcher:    TimeInterval{Open: nine_am, Close: five_pm},
		Staff:         TimeInterval{Open: nine_am, Close: five_pm}}

	civil_cafeteria := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: twelve_am, Close: three_pm},
		Student:       TimeInterval{Open: twelve_am, Close: three_pm},
		Professor:     TimeInterval{Open: twelve_am, Close: three_pm},
		Researcher:    TimeInterval{Open: twelve_am, Close: three_pm},
		Staff:         TimeInterval{Open: twelve_am, Close: three_pm}}

	sena := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: eight_am, Close: seven_pm},
		Student:       TimeInterval{Open: eight_am, Close: seven_pm},
		Professor:     TimeInterval{Open: eight_am, Close: seven_pm},
		Researcher:    TimeInterval{Open: eight_am, Close: seven_pm},
		Staff:         TimeInterval{Open: eight_am, Close: seven_pm}}

	SAS := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: nine_am, Close: nine_pm},
		Student:       TimeInterval{Open: nine_am, Close: nine_pm},
		Professor:     TimeInterval{Open: nine_am, Close: nine_pm},
		Researcher:    TimeInterval{Open: nine_am, Close: nine_pm},
		Staff:         TimeInterval{Open: nine_am, Close: nine_pm}}

	Math := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: one_half_pm, Close: three_pm},
		Student:       TimeInterval{Open: one_half_pm, Close: three_pm},
		Professor:     TimeInterval{Open: twelve_am, Close: three_pm},
		Researcher:    TimeInterval{Open: twelve_am, Close: three_pm},
		Staff:         TimeInterval{Open: twelve_am, Close: three_pm}}

	Red := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: eight_am, Close: ten_pm},
		Student:       TimeInterval{Open: eight_am, Close: ten_pm},
		Professor:     TimeInterval{Open: eight_am, Close: ten_pm},
		Researcher:    TimeInterval{Open: eight_am, Close: ten_pm},
		Staff:         TimeInterval{Open: eight_am, Close: ten_pm}}

	Green := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: seven_am, Close: seven_pm},
		Student:       TimeInterval{Open: seven_am, Close: seven_pm},
		Professor:     TimeInterval{Open: seven_am, Close: seven_pm},
		Researcher:    TimeInterval{Open: seven_am, Close: seven_pm},
		Staff:         TimeInterval{Open: seven_am, Close: seven_pm}}

	CTN_cafeteria := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: twelve_am, Close: two_pm},
		Student:       TimeInterval{Open: twelve_am, Close: two_pm},
		Professor:     TimeInterval{Open: twelve_am, Close: two_pm},
		Researcher:    TimeInterval{Open: twelve_am, Close: two_pm},
		Staff:         TimeInterval{Open: twelve_am, Close: two_pm}}

	CTN_bar := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: eight_half_am, Close: four_half_pm},
		Student:       TimeInterval{Open: eight_half_am, Close: four_half_pm},
		Professor:     TimeInterval{Open: eight_half_am, Close: four_half_pm},
		Researcher:    TimeInterval{Open: eight_half_am, Close: four_half_pm},
		Staff:         TimeInterval{Open: eight_half_am, Close: four_half_pm}}
	//	openingHours := map[string]TimeInterval{
	//		GeneralPublic: TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
	//		Student:       TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
	//		Professor:     TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
	//		Researcher:    TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
	//		Staff:         TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours}}

	//	openingHoursnd := map[string]TimeInterval{
	//		GeneralPublic: TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
	//		Student:       TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
	//		Professor:     TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
	//		Researcher:    TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
	//		Staff:         TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours}}

	addPlace("Central Bar", "BAR", Coordinates{Lat: 38.736606, Lng: -9.139532}, nine_to_five, "Alameda")
	addPlace("Civil Bar", "BAR", Coordinates{Lat: 38.736988, Lng: -9.139955}, nine_to_five, "Alameda")
	addPlace("Civil Cafeteria", "RESTAURANT", Coordinates{Lat: 38.737650, Lng: -9.140384}, civil_cafeteria, "Alameda")
	addPlace("Sena Pastry Shop", "RESTAURANT", Coordinates{Lat: 38.737677, Lng: -9.138672}, sena, "Alameda")
	addPlace("Mechy Bar", "BAR", Coordinates{Lat: 38.737247, Lng: -9.137434}, nine_to_five, "Alameda")
	addPlace("AEIST Bar", "BAR", Coordinates{Lat: 38.736542, Lng: -9.137226}, nine_to_five, "Alameda")
	addPlace("AEIST Esplanade", "BAR", Coordinates{Lat: 38.736318, Lng: -9.137820}, nine_to_five, "Alameda")
	addPlace("Chemy Bar", "BAR", Coordinates{Lat: 38.736240, Lng: -9.138302}, nine_to_five, "Alameda")
	addPlace("SAS Cafeteria", "RESTAURANT", Coordinates{Lat: 38.736571, Lng: -9.137036}, SAS, "Alameda")
	addPlace("Math Cafeteria", "RESTAURANT", Coordinates{Lat: 38.735508, Lng: -9.139645}, Math, "Alameda")
	addPlace("Complex Bar", "BAR", Coordinates{Lat: 38.736050, Lng: -9.140156}, nine_to_five, "Alameda")

	addPlace("Tagus Cafeteria", "RESTAURANT", Coordinates{Lat: 38.737802, Lng: -9.303223}, civil_cafeteria, "Taguspark")
	addPlace("Red Bar", "BAR", Coordinates{Lat: 38.736546, Lng: -9.302207}, Red, "Taguspark")
	addPlace("Green Bar", "BAR", Coordinates{Lat: 38.738004, Lng: -9.303058}, Green, "Taguspark")

	addPlace("CTN Cafeteria", "RESTAURANT", Coordinates{Lat: 38.812522, Lng: -9.093773}, CTN_cafeteria, "CTN")
	addPlace("CTN Bar", "BAR", Coordinates{Lat: 38.812522, Lng: -9.093773}, CTN_bar, "CTN")
}

func main() {
	initPlaces()
	finish := make(chan bool)

	// r := new(regression.Regression)

	// r.Train(regression.DataPoint(1,[]float64{2}))
	// r.Train(regression.DataPoint(1,[]float64{2}))
	// r.Train(regression.DataPoint(1,[]float64{2}))
	// r.Train(regression.DataPoint(1,[]float64{2}))

	places["Civil Bar"].Regression.Train(regression.DataPoint(2, []float64{1}),
		regression.DataPoint(2, []float64{1}),
		regression.DataPoint(1, []float64{0}),
		regression.DataPoint(1, []float64{0}),
		regression.DataPoint(3, []float64{2}),
		regression.DataPoint(3, []float64{2}),
	)
	//r.Train(regression.DataPoint(2,[]float64{4}))
	//r.Train(regression.DataPoint(6,[]float64{120}))
	places["Civil Bar"].Regression.Run()

	log.Println("Regression formula:\n%v\n", places["Civil Bar"].Regression.Formula)
	log.Println("Regression:\n%s\n", places["Civil Bar"].Regression)
	prediction, _ := places["Civil Bar"].Regression.Predict([]float64{2})
	log.Println("Predict:\n%s\n", prediction)

	muxhttp := http.NewServeMux()
	muxhttp.HandleFunc("/register", registerHandler)
	muxhttp.HandleFunc("/login", loginHandler)
	muxhttp.HandleFunc("/logout", logoutHandler)
	muxhttp.HandleFunc("/addImage", addImageHandler)
	muxhttp.HandleFunc("/addMenu", addMenuHandler)
	muxhttp.HandleFunc("/rateMenu", rateMenuHandler)
	muxhttp.HandleFunc("/getMenuRates", getMenuRatesHandler)
	muxhttp.HandleFunc("/getCanteenRates", getCanteenRatesHandler)
	muxhttp.HandleFunc("/predict", getCanteensHandler)
	muxhttp.HandleFunc("/getMenus", getMenusHandler)
	muxhttp.HandleFunc("/getImages", getImagesHandler)
	muxhttp.HandleFunc("/checkImageNames", getImagesNameHandler)
	muxhttp.HandleFunc("/queue", queueHandler)
	muxhttp.HandleFunc("/prefetch", prefetchMenuImages)
	muxhttp.HandleFunc("/addImageProfile", addImageProfileHandler)

	config_tls := &tls.Config{
		MinVersion:               tls.VersionTLS12,
		CurvePreferences:         []tls.CurveID{tls.CurveP521, tls.CurveP384, tls.CurveP256},
		PreferServerCipherSuites: true,
		CipherSuites: []uint16{
			tls.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
			tls.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
			tls.TLS_RSA_WITH_AES_256_GCM_SHA384,
			tls.TLS_RSA_WITH_AES_256_CBC_SHA,
		},
	}

	server_http_tls := &http.Server{
		Addr:         ":443",
		Handler:      muxhttp,
		TLSConfig:    config_tls,
		TLSNextProto: make(map[string]func(*http.Server, *tls.Conn, http.Handler), 0),
	}

	go func() {
		log.Println("Serving Http")
		//log.fatal(server_http_tls.ListenAndServeTLS("../../ssl/server.crt", "../../ssl/server.key"))
		server_http_tls.ListenAndServeTLS("ssl/server_tls.crt", "ssl/server_tls.key")
		//http.ListenAndServe(":8000", muxhttp)
	}()

	<-finish
}
