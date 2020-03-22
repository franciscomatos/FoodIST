package main

import(
	"log"
	"net/http"
	"encoding/json"
	"time"
	"strconv"
)


/*
	Works as a "security" level
*/

const ( //0,1,...
	General_Public= iota
	Student 
	Professor
	Researcher
	Staff
)

//
const ( //0,1,...
	Meat= iota
	Fish 
	Vegetarian
	Vegan
)


//Rest API structs
type RegisterRequest struct {
	Username    string 	`json:"username"`
	Password	string 	`json:"password"`
	Level		int 	`json:"level"`
	Restrictions []bool	`json:"restrictions"`
}

type RegisterResponse struct {
	Status string `json:"status"`
}

type LoginRequest struct {
	Username     string `json:"username"`
	Password 	string `json:"password"`
}

type LoginResponse struct {
	Status string `json:"status"`
}

type LogoutRequest struct {
	Username     string `json:"username"`
	Password 	string `json:"password"`
}

type LogoutResponse struct {
	Status string `json:"status"`
}

type AddMenuRequest struct { //TODO: ADD the dietry restritions
	NameMenu    	string `json:"namemenu"`
	NameCanteen    	string `json:"namecanteen"`
	Price 			string `json:"price"`
	Username    	string `json:"username"`
	Password 		string `json:"password"`

}

type AddMenuResponse struct {
	Status string `json:"status"`
}

type AddImageRequest struct {
	NameMenu    	string `json:"namemenu"`
	NameCanteen    	string `json:"namecanteen"`
	NameImage    	string `json:"nameimage"`
	Image    		string `json:"image"`
	Username    	string `json:"username"`
	Password 		string `json:"password"`

}

type AddImageResponse struct {
	Status string `json:"status"`
}


//Business Logic structs
type Coordinates struct {
	Lat     float64
	Lng     float64
}
type TimeInterval struct {
	Open	time.Time
	Close	time.Time
}
type Canteen struct {
	Menus		map[string]*Menu
	Location	Coordinates
	OpenHours	map[int]TimeInterval
}

type Menu struct {
	Price		float64 	//should work?
	Gallery		[]Image		//slice of images
}

type Image struct {
	Image	string 	//base64
	Name	string	//TODO: maybe transform this into timestamp+username
}

type User struct {
	Password		string
	Level			int 
	Restrictions 	[]bool
	LoggedIn		bool
}

//Global Variables
var users = make(map[string]User) //Key is user and Value is password

var places = make(map[string]Canteen) // Key is the place name and Value is its contents

//Aux Functions
func validadeUser( username, password string)	(*User,string,int){

	if len(username) == 0{
		return nil,"Empty Username", http.StatusUnauthorized
	}

	if len(password) == 0{
		return nil,"Empty Password", http.StatusUnauthorized
	}

	user, ok := users[username]
	if !ok{
		return nil,"User does not exist", http.StatusUnauthorized
	}

	if user.Password != password{
		return nil,"Wrong Password", http.StatusUnauthorized
	}

	if ok && !user.LoggedIn{
		return &user,"Not Logged in", http.StatusUnauthorized
	}

	if ok && user.LoggedIn && user.Password == password{
		return &user, "OK" , http.StatusOK
	}

	return nil, "Unexpected error" , http.StatusBadRequest
}
func validadeCanteen( canteenName string)	(*Canteen,string,int){

	if len(canteenName) == 0{
		return nil,"Empty Canteen Name", http.StatusBadRequest
	}

	canteen, ok := places[canteenName]
	if !ok{
		return nil,"Canteen does not exist", http.StatusBadRequest
	}

	return &canteen, "OK" , http.StatusOK
	

}
//Handlers
func registerHandler(w http.ResponseWriter, r *http.Request) {
	var userRequest RegisterRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New register  received")
	log.Println("Request body: Username: "+userRequest.Username+" Password: "+userRequest.Password)
	
	//test if the user already exists
	_, ok := users[userRequest.Username]
	if ok{
		log.Println("[ERROR] User already created")
		http.Error(w, "User already created", http.StatusBadRequest)
		return
	}

	if len(userRequest.Username) == 0{
		log.Println("[ERROR] Empty Username")
		http.Error(w, "Empty Username", http.StatusBadRequest)
		return
	}

	if len(userRequest.Password) == 0{
		log.Println("[ERROR] Empty Password")
		http.Error(w, "Empty Password", http.StatusBadRequest)
		return
	}

	users[userRequest.Username] = 	User{	Password : userRequest.Password, 
											Level : userRequest.Level, 
											Restrictions : userRequest.Restrictions,
											LoggedIn : false}
	//FIXME: This is probably not needed
	response := RegisterResponse{
		Status:			"OK"}

	json.NewEncoder(w).Encode(response)
}
//TODO: This way the app has to also logout, maybe not needed?
func loginHandler(w http.ResponseWriter, r *http.Request) { 

	var userRequest LoginRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New login  received")
	log.Println("Request body: Username: "+userRequest.Username+" Password: "+userRequest.Password)
	
	//test if the user exists
	user, stmt ,status := validadeUser(userRequest.Username,userRequest.Password)
	if user == nil{
		log.Println("[ERROR] ",stmt)
		http.Error(w,stmt, status)
		return
	}

	user.LoggedIn = true

	response := LoginResponse{
		Status:			"OK"}
		
	json.NewEncoder(w).Encode(response)
}

func logoutHandler(w http.ResponseWriter, r *http.Request) { 

	var userRequest LogoutRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New logout  received")
	log.Println("Request body: Username: "+userRequest.Username+" Password: "+userRequest.Password)
	
	//test if the user exists
	user, stmt, status := validadeUser(userRequest.Username,userRequest.Password)
	if status != http.StatusOK{
		log.Println("[ERROR] ",stmt)
		http.Error(w, stmt, status)
		return
	}

	user.LoggedIn = false

	response := LogoutResponse{
		Status:			"OK"}
		
	json.NewEncoder(w).Encode(response)
}

func addMenuHandler(w http.ResponseWriter, r *http.Request){

	var userRequest AddMenuRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New menu  received")
	log.Println("Request body: NameMenu: ",userRequest.NameMenu," Price: ",userRequest.Price," Canteen: ",userRequest.NameCanteen)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username,userRequest.Password)
	if status != http.StatusOK{
		log.Println("[ERROR] ",stmt)
		http.Error(w,stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.NameCanteen)
	if status != http.StatusOK{
		log.Println("[ERROR] ",stmt)
		http.Error(w,stmt, status)
		return
	}

	if s, err := strconv.ParseFloat(userRequest.Price, 32); err == nil {
		canteen.Menus[userRequest.NameMenu] = &Menu{Price : s}
	}else{
		log.Println("[ERROR] Bad Price value")
		http.Error(w,"Bad Price value", http.StatusBadRequest)
		return
	}

	response := AddMenuResponse{
		Status:			"OK"}
		
	json.NewEncoder(w).Encode(response)
}

func addImageHandler(w http.ResponseWriter, r *http.Request){

	var userRequest AddImageRequest
	json.NewDecoder(r.Body).Decode(&userRequest)

	log.Println("New menu  received")
	log.Println("Request body: NameMenu: ",userRequest.NameMenu, "Canteen: ",userRequest.NameCanteen)

	//test if the user already exists
	_, stmt, status := validadeUser(userRequest.Username,userRequest.Password)
	if status != http.StatusOK{
		log.Println("[ERROR] ",stmt)
		http.Error(w,stmt, status)
		return
	}

	canteen, stmt, status := validadeCanteen(userRequest.NameCanteen)
	if status != http.StatusOK{
		log.Println("[ERROR] ",stmt)
		http.Error(w,stmt, status)
		return
	}

	canteen.Menus[userRequest.NameMenu].Gallery = append(canteen.Menus[userRequest.NameMenu].Gallery,Image{Name : userRequest.NameImage, Image : userRequest.Image})

	response := AddImageResponse{
		Status:			"OK"}
		
	json.NewEncoder(w).Encode(response)
}
// INITIALIZATION FUNCTIONS

func addPlace( name string, coord Coordinates, times map[int]TimeInterval ){
	places[name] = 	Canteen{	Menus : make(map[string]*Menu),
								Location : coord,
								OpenHours : times}
}

func initPlaces(){ // initiate more if needed
	timeLayout := "15:04:05"
	MonicaOpenHours, _ := time.Parse(timeLayout, "08:00:00")
	MonicaCloseHours, _ := time.Parse(timeLayout, "17:00:00")

	openingHours := map[int]TimeInterval{
		General_Public 	: TimeInterval{Open : MonicaOpenHours, Close : MonicaCloseHours},
		Student 		: TimeInterval{Open : MonicaOpenHours, Close : MonicaCloseHours},
		Professor		: TimeInterval{Open : MonicaOpenHours, Close : MonicaCloseHours},
		Researcher		: TimeInterval{Open : MonicaOpenHours, Close : MonicaCloseHours},
		Staff			: TimeInterval{Open : MonicaOpenHours, Close : MonicaCloseHours}}

	addPlace("Monica",Coordinates{Lat : 38.737389, Lng : -9.137358}, openingHours)
}

func main() {
	initPlaces()
	finish := make(chan bool)

	mux_http := http.NewServeMux()
	mux_http.HandleFunc("/register", registerHandler)
	mux_http.HandleFunc("/login", loginHandler)
	mux_http.HandleFunc("/logout", logoutHandler)
	mux_http.HandleFunc("/addImage", addImageHandler)
	mux_http.HandleFunc("/addMenu", addMenuHandler)
	// mux_http.HandleFunc("/rateImage", scoreHandler)
	// mux_http.HandleFunc("/rateImage", scoreHandler)

	go func() {
		log.Println("Serving HTTP")
		http.ListenAndServe(":8000", mux_http)
	}()

	<-finish
}


