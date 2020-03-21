package main

import(
	"log"
	"net/http"
	"encoding/json"
	"time"
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
	Menus		map[string]Menu
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
	
	//test if the user already exists
	user, ok := users[userRequest.Username]
	if !ok{
		log.Println("[ERROR] User does not exist")
		http.Error(w, "User does not exist", http.StatusBadRequest)
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

	if userRequest.Password != user.Password{
		log.Println("[ERROR] Wrong Password")
		http.Error(w, "Wrong Password", http.StatusBadRequest)
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

	log.Println("New login  received")
	log.Println("Request body: Username: "+userRequest.Username+" Password: "+userRequest.Password)
	
	//test if the user already exists
	user, ok := users[userRequest.Username]
	if !ok{
		log.Println("[ERROR] User does not exist")
		http.Error(w, "User does not exist", http.StatusBadRequest)
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

	if userRequest.Password != user.Password{
		log.Println("[ERROR] Wrong Password")
		http.Error(w, "Wrong Password", http.StatusBadRequest)
		return
	}

	user.LoggedIn = false

	response := LogoutResponse{
		Status:			"OK"}
		
	json.NewEncoder(w).Encode(response)
}

// INITIALIZATION FUNCTIONS

func addPlace( name string, coord Coordinates, times map[int]TimeInterval ){
	places[name] = 	Canteen{	Menus : make(map[string]Menu),
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
	// mux_http.HandleFunc("/addPhoto", showHandler)
	// mux_http.HandleFunc("/addMenu", showHandler)
	// mux_http.HandleFunc("/rateImage", scoreHandler)
	// mux_http.HandleFunc("/rateImage", scoreHandler)

	go func() {
		log.Println("Serving HTTP")
		http.ListenAndServe(":8000", mux_http)
	}()

	<-finish
}


