package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class fetchFoodServiceRatings extends fetchBaseCustom {
    private String foodService;
    private TextView averageBigView;
    private RatingBar averageRatingBar;
    private TextView ratingsCounter;
    private AnyChartView ratingChartView;

    public fetchFoodServiceRatings(GlobalClass global, String foodService, TextView averageBigView,
                                   RatingBar averageRatingBar, TextView ratingsCounter,
                                   AnyChartView ratingChartView) {
        super(global, global.getURL() + "/getCanteenRates");
        this.foodService = foodService;
        this.averageBigView = averageBigView;
        this.averageRatingBar = averageRatingBar;
        this.ratingsCounter = ratingsCounter;
        this.ratingChartView = ratingChartView;
    }

    @Override
    protected String buildBody() {

        return  "{\"username\":\"" + getGlobal().getUser().getUsername() + "\"," +
                "\"password\":\"" + getGlobal().getUser().getPassword() +"\"," +
                "\"canteen\":\""+foodService+"\"}";
    }

    @Override
    protected void parse(String data) {

        Log.i("FETCH FOOD SERVICE RATINGS", "parsing response ...");

        try {
            JSONObject response = new JSONObject(data);
            if(!response.getString("status").equals("OK"))
                throw new JSONException("Json wasn't ok");

            JSONArray ratings = response.getJSONArray("ratings");
            Menu menu = getGlobal().getFoodService(foodService).getMenu();
            menu.clearRatings();
            //add ratings to dish
            for(int i = 0 ; i < ratings.length() ; i++) {
                Integer rating = ratings.getInt(i);
                menu.addRating(rating);
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
        Menu menu  = getGlobal().getFoodService(foodService).getMenu();

        DecimalFormat df = new DecimalFormat("#.##");
        averageBigView.setText(df.format(menu.computeRatingAverage()));

        averageRatingBar.setRating(menu.computeRatingAverage().floatValue());

        ratingsCounter.setText(menu.computeNumberOfRatings().toString());


        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for(Map.Entry<Integer, Integer> classification: menu.getRatings().entrySet()) {
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
        Log.i("FETCH FOOD SERVICE RATINGS", "finished fetching");
    }
}
