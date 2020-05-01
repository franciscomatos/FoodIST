package pt.ulisboa.tecnico.cmov.foodist.domain;
import android.graphics.Bitmap;
import java.util.Date;
public class AppImage {

    private String foodService;
    private String dish;
    private Date  timestamp;
    private Bitmap image;
    private boolean thumbnail;
    private String username;

    //FIXME: Change the size of  the thumbnail if needed
    //private int  TWIDTH = 500;
    //private int  THEIGHT = 500;

    public AppImage(String foodService, String dish, Date timestamp, String username, Bitmap image, boolean thumbnail) {
        this.foodService = foodService;
        this.dish = dish;
        this.timestamp = timestamp;
        this.image = image;
        this.username = username; //doesnt need getter nor setter
        this.thumbnail = thumbnail; //doesnt need getter nor setter
        //this.thumbnail = Bitmap.createScaledBitmap(image, TWIDTH, THEIGHT, false); // create the thumbnail it self to avoid more data usage
    }

    public String getFoodService() {
        return foodService;
    }

    public void setFoodService(String foodService) {
        this.foodService = foodService;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    public boolean isThumbnail() {
        return this.thumbnail;
    }

    @Override
    public String toString() { //"T_CIVIL_TOSTA_0800_pedro timestamp is diferent tho"
        return  this.thumbnail ? "T_" : "F_" + this.foodService + "_" + this.dish + "_" + String.valueOf(timestamp.getTime()) + "_" + this.username; //where timestamp is the number of miliseconds
    }
}
