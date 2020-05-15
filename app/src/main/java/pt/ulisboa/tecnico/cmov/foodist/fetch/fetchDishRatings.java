package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class fetchDishRatings extends fetchBaseCustom {
    private String foodServiceName;
    private String dishName;
    private Integer dishIndex;

    private TextView averageBigView;
    private RatingBar averageRatingBar;
    private TextView ratingsCounter;
    private AnyChartView ratingChartView;

    public fetchDishRatings(GlobalClass global, String foodService, String dish, Integer dishIndex,
                            TextView averageBigView, RatingBar averageRatingBar, TextView ratingsCounter,
                            AnyChartView ratingChartView) {
        super(global, global.getURL() + "/getMenuRates");
        this.foodServiceName = foodService;
        this.dishName = dish;
        this.dishIndex = dishIndex;
        this.averageBigView = averageBigView;
        this.averageRatingBar = averageRatingBar;
        this.ratingsCounter = ratingsCounter;
        this.ratingChartView = ratingChartView;
    }

    @Override
    protected String buildBody() {

        return  "{\"username\":\"" + getGlobal().getUser().getUsername() + "\"," +
                "\"password\":\"" + getGlobal().getUser().getPassword() +"\"," +
                "\"canteen\":\""+foodServiceName+"\"," +
                "\"menu\":\"" + dishName +"\"}";
    }

    @Override
    protected void parse(String data) {

        Log.i("FETCH DISH RATINGS", "parsing response ...");

        try {
            JSONObject response = new JSONObject(data);
            if(!response.getString("status").equals("OK"))
                throw new JSONException("Json wasn't ok");

            JSONArray ratings = response.getJSONArray("ratings");
            Dish dish = getGlobal().getFoodService(foodServiceName).getMenu().getDish(dishIndex);
            dish.clearRatings();
            //add ratings to dish
            for(int i = 0 ; i < ratings.length() ; i++) {
                Integer rating = ratings.getInt(i);
                System.out.println("RATING:" + rating);
                dish.addRating(rating);
            }

        } catch (JSONException e) {
            Log.e("ERROR", ": Failed to parse the json");
            e.printStackTrace();
        }catch (Exception e) {
            Log.e("ERROR", data);
            e.printStackTrace();
        }
    }

    protected void onPostExecute(Void aVoid) {
        //will update the activity
        Dish dish = getGlobal().getFoodService(foodServiceName).getMenu().getDish(dishIndex);

        DecimalFormat df = new DecimalFormat("#.##");
        averageBigView.setText(df.format(dish.computeRatingAverage()));

        averageRatingBar.setRating(dish.computeRatingAverage().floatValue());

        ratingsCounter.setText(dish.computeNumberOfRatings().toString());


        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for(Map.Entry<Integer, Integer> classification: dish.getRatings().entrySet()) {
            data.add(new ValueDataEntry(classification.getKey(), classification.getValue()));
        }

        for(DataEntry entry: data) {
            Toast.makeText(getGlobal(), entry.generateJs(), Toast.LENGTH_LONG).show();
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        //cartesian.animation(true);
        cartesian.yScale().minimum(0d);

        //cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        //cartesian.interactivity().hoverMode(HoverMode.BY_X);


        ratingChartView.setChart(cartesian);

        Log.i("FETCH DISH RATINGS", "finished fetching");
    }
}
