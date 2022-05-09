package com.example.bkgreenhouse;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoistureFragment extends Fragment {
    public String getName() {
        return name;
    }

    private final String name = "Độ ẩm đất";
    private Spinner spinner;
    private ApiUrl apiUrl;
    private LineChart lineChart;
    DataGroup dataGroup;
    public CustomProgressDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moisture, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new CustomProgressDialog(getContext());
        apiUrl = new ApiUrl();
        spinner = view.findViewById(R.id.spinner_date_moisture);
        lineChart = view.findViewById(R.id.line_chart_moisture);
        initSpinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initLineChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initSpinner() {
        ItemAdapter dateAdapter = new ItemAdapter(getContext(), R.layout.item_spiner_selected, getDateList(31));
        spinner.setAdapter(dateAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<SpinnerItem> getDateList(int count) {
        List<SpinnerItem> list = new ArrayList<>();
        for (int i = 1; i<= count; i++){
            Date date = Date.from(Instant.now().minus(i, ChronoUnit.DAYS));
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = formatter.format(date);
            list.add(new SpinnerItem(strDate));
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initLineChart(){
        dialog.show();
        String sD = getISODay((int) spinner.getSelectedItemPosition()+1);
        String eD = getISODay((int) spinner.getSelectedItemPosition());
        Log.d("detail", "SD: "+sD +"  ED: " + eD);
        new MoistureFragment.GetData(sD, eD).execute(apiUrl.getMOISTURE_GET());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getISODay(int amount){
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).with(LocalTime.MIN).minus(amount, ChronoUnit.DAYS).minusHours(7);
        return now.format(formatter);
    }

    class GetData extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String startTime, endTime;

        GetData(String startTime, String endTime){
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        protected String doInBackground(String... strings) {
            String params = "?start_time=" +startTime+ "&end_time=" + endTime;
            String url = strings[0] + params;
            Log.d("detail", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String s) {
            initChartData(s);
            super.onPostExecute(s);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initChartData(String s) {
        dataGroup = new DataGroup();
        ArrayList<Entry> dataSet = new ArrayList<Entry>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<DataPointModel>>(){}.getType();
        List<DataPointModel> dataPointList = gson.fromJson(s, type);
        for (DataPointModel dataPoint : dataPointList){
            Date date = convertDateFromISO(dataPoint.created_at);
            String createAt = date.toString().split(" ")[3].substring(0,5);
            dataGroup.addDataPoint(Float.parseFloat(dataPoint.value), createAt);
        }
        for (int i = 1; i <= 3; i++){
            dataSet.add(new Entry(i, dataGroup.getValueOf(i)));
        }
        initLineChartDataSet(dataSet);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Date convertDateFromISO(String value) {
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(value);
        Instant i = Instant.from(ta);
        return Date.from(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initLineChartDataSet(ArrayList<Entry> dataSet) {
        lineChart.setNoDataText("Không có dữ liệu");
        LineDataSet lineDataSet = new LineDataSet(dataSet, "Nhiệt độ");
        ArrayList<ILineDataSet> iLineDataSets =  new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setAxisLineWidth(1);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getXAxis().setTextSize(15);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getAxisLeft().setTextSize(15);
        lineChart.getAxisRight().setTextSize(15);
        lineChart.getXAxis().setLabelCount(3);
        lineChart.getXAxis().setTextColor(Color.RED);
        lineChart.getAxisLeft().setTextColor(Color.RED);
        lineChart.setExtraBottomOffset(10f);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value){
                    case 1: return "Sáng";
                    case 2: return "Chiều";
                    case 3: return "Tối";
                    default: return "";
                }
            }
        });
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setCircleHoleRadius(5);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setValueTextSize(15);
        dialog.hide();
    }
}