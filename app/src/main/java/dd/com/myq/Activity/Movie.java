package dd.com.myq.Activity;
import android.util.Log;
import android.widget.ImageView;

public class Movie {
    private String title,id;//, genre, year;
    private int image;

    private String wasteimage;


    public Movie() {
    }
//
    public Movie(String title,String imag){//},int imag ){//String imag){
        this.title = title;
        //this.image = imag;
        this.wasteimage = imag;
    }

//    public Movie(String title,int imag){//},int imag ){//String imag){
//        this.title = title;
//        this.image = imag;
//        //this.wasteimage = imag;
//    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public int getImage() {
        return image;
    }

    public void setId(String id)
    {this.id=id;}

    public String getId() {
        return id;
    }

    public String getWasteImage() {
        return wasteimage;
    }

}