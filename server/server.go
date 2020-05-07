package main

import (
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
	GeneralPublic = "0"
	Student       = "1"
	Professor     = "2"
	Researcher    = "3"
	Staff         = "4"
)

//
const ( //0,1,...
	Meat = iota
	Fish
	Vegetarian
	Vegan
)

var SIZE = 10


//Rest API structs
type RegisterRequest struct {
	Username string   `json:"username"`
	Password string   `json:"password"`
	Level    string   `json:"level"`   //FIXME: verify field
	Dietary  []string `json:"dietary"` //FIXME: Test for size!
}

type RegisterResponse struct {
	Status string `json:"status"`
}

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type LoginResponse struct {
	Status string `json:"status"`
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

type AddImageRequest struct {
	NameMenu    string `json:"namemenu"`
	NameCanteen string `json:"namecanteen"`
	NameImage   string `json:"nameimage"`
	Image       string `json:"image"`
	Username    string `json:"username"`
	Password    string `json:"password"`
}

type AddImageResponse struct {
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

type MenusInterface struct {
	Name    string  `json:"name"`
	Price   float64 `json:"price"`
	Dietary string  `json:"dietary"`
	Ratings float64 `json:"ratings"`
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

type CanteenInterface struct {
	Name      		string       `json:"name"`
	Prediction      int          `json:"prediction"`
}

type GetImagesRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	Canteen  string `json:"canteen"`
	Menu     string `json:"menu"`
	Page     int    `json:"page,string"`
}
type GetImagesResponse struct {
	Images []Image `json:"images"`
	Status string  `json:"status"`
}

type QueueRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	Canteen  string `json:"canteen"`
	Minutes  string `json:"minutes"`
}
type QueueResponse struct {
	Status string `json:"status"`
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
	Ratings map[string]int //TODO: Update this part with more info (more detailed breakdown of user ratings (e.g.histogram))
}

type Image struct {
	Name  string `json:"name"`
	Image string `json:"image"`
}

type User struct {
	Password string
	Level    string
	Dietary  []string
	LoggedIn bool
	InQueue  Position
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
		Level:    userRequest.Level,
		Dietary:  userRequest.Dietary,
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
		Status: "OK"}

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

	log.Println("New Image Received")
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

	canteen.Menus[userRequest.NameMenu].Gallery = append(canteen.Menus[userRequest.NameMenu].Gallery, Image{Name: userRequest.NameImage, Image: userRequest.Image})

	response := AddImageResponse{
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

	count := 0
	sum := 0
	for _, value := range canteen.Menus[userRequest.NameMenu].Ratings {
		count++
		sum += value
	}

	response := RateMenuResponse{
		Status:    "OK",
		Average:   float64(sum) / float64(count),
		NumRating: count}

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
		prediction, err := value.Regression.Predict([]float64{float64(len(value.Queue))})
		if err != nil {
			prediction2 := int(prediction)
			if value.Campus == userRequest.Campus {
				canteens = append(canteens, CanteenInterface{
					Name:      	key,
					Prediction:	prediction2})
			}
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
		log.Println("[DEBUG] ", value)
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
				Ratings: 0})
		} else {
			menus = append(menus, MenusInterface{
				Price:   value.Price,
				Name:    key,
				Dietary: value.Dietary,
				Ratings: float64(sum) / float64(count)})
		}
	}
	response := GetMenusResponse{
		Status: "OK",
		Menus:  menus}

	json.NewEncoder(w).Encode(response)
}

func getImagesHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest GetImagesRequest
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
	if status != http.StatusOK {
		log.Println("[ERROR] ", stmt)
		http.Error(w, stmt, status)
		return
	}
	begin := SIZE * userRequest.Page
	end := SIZE*userRequest.Page + SIZE

	var response GetImagesResponse

	if end >= len(canteen.Menus[userRequest.Menu].Gallery) {
		response = GetImagesResponse{
			Status: "OK",
			Images: canteen.Menus[userRequest.Menu].Gallery[begin:]}
	} else {
		response = GetImagesResponse{
			Status: "OK",
			Images: canteen.Menus[userRequest.Menu].Gallery[begin:end]}
	}
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

	if user.InQueue == (Position{}) { //if it isnt in a queue add it

		log.Println("New request to enter queue Received")
		log.Println(userRequest)

		minutes, err := strconv.Atoi(userRequest.Minutes)

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
				canteen.Regression.Train(regression.DataPoint(float64(user.InQueue.Number), []float64{float64(user.InQueue.Minutes)}))
				canteen.Regression.Run()
				
				canteen.Queue = append(canteen.Queue[:i], canteen.Queue[i+1:]...)
				user.InQueue = Position{} //empty position
			}
		}
	}

	response := QueueResponse{
		Status: "OK"}

	json.NewEncoder(w).Encode(response)
}

// INITIALIZATION FUNCTIONS

func addPlace(name string, typ string, coord Coordinates, times map[string]TimeInterval, campus string) {
	places[name] = &Canteen{Menus: make(map[string]*Menu),
		Type:      typ,
		Location:  coord,
		OpenHours: times,
		Campus:    campus,
		Regression: new(regression.Regression)}
}

func initPlaces() { // initiate more if needed
	timeLayout := "15:04:05"
	MonicaOpenHours, _ := time.Parse(timeLayout, "08:00:00")
	MonicaCloseHours, _ := time.Parse(timeLayout, "17:00:00")
	AbilioOpenHours, _ := time.Parse(timeLayout, "10:00:00")
	AbilioCloseHours, _ := time.Parse(timeLayout, "20:00:00")

	openingHours := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
		Student:       TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
		Professor:     TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
		Researcher:    TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours},
		Staff:         TimeInterval{Open: AbilioOpenHours, Close: AbilioCloseHours}}

	openingHoursnd := map[string]TimeInterval{
		GeneralPublic: TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
		Student:       TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
		Professor:     TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
		Researcher:    TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours},
		Staff:         TimeInterval{Open: MonicaOpenHours, Close: MonicaCloseHours}}

	addPlace("CIVIL", "BAR", Coordinates{Lat: 38.737389, Lng: -9.137358}, openingHours, "Alamenda")
	addPlace("AE", "RESTAURANT", Coordinates{Lat: 38.737389, Lng: -9.137358}, openingHoursnd, "Alamenda")
	addPlace("GreenBar Tagus", "BAR", Coordinates{Lat: 38.737389, Lng: -9.137358}, openingHours, "Taguspark")
	addPlace("Cafetaria", "RESTAURANT", Coordinates{Lat: 38.737389, Lng: -9.137358}, openingHoursnd, "Taguspark")
}

func main() {
	initPlaces()
	finish := make(chan bool)

	r := new(regression.Regression)

	r.Train(regression.DataPoint(1,[]float64{2}))
	r.Train(regression.DataPoint(1,[]float64{2}))
	r.Train(regression.DataPoint(1,[]float64{2}))
	r.Train(regression.DataPoint(1,[]float64{2}))

	//r.Train(regression.DataPoint(2,[]float64{4}))
	//r.Train(regression.DataPoint(6,[]float64{120}))
	r.Run()
	log.Println("Regression formula:\n%v\n", r.Formula)
	log.Println("Regression:\n%s\n", r)
	prediction, _ := r.Predict([]float64{2})
	log.Println("Predict:\n%s\n", prediction)


	muxhttp := http.NewServeMux()
	muxhttp.HandleFunc("/register", registerHandler)
	muxhttp.HandleFunc("/login", loginHandler)
	muxhttp.HandleFunc("/logout", logoutHandler)
	muxhttp.HandleFunc("/addImage", addImageHandler)
	muxhttp.HandleFunc("/addMenu", addMenuHandler)
	muxhttp.HandleFunc("/rateMenu", rateMenuHandler)
	muxhttp.HandleFunc("/predict", getCanteensHandler)
	muxhttp.HandleFunc("/getMenus", getMenusHandler)
	muxhttp.HandleFunc("/getImages", getImagesHandler)
	muxhttp.HandleFunc("/queue", queueHandler)

	go func() {
		log.Println("Serving HTTP")
		http.ListenAndServe(":8000", muxhttp)
	}()

	<-finish
}
