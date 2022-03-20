package com.example.mydoppler;

import android.os.Bundle;

import com.jasperlu.doppler.Doppler;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class MainActivity extends AppCompatActivity {

    // 页面元素
    private TextView promptTextView;

    // 多普勒
    private Doppler doppler;

    // doppler模块获取的数据
    private XYSeries mSeries;
    // 画基准线的数据
    private XYSeries div10;
    // 画图的view
    private GraphicalView mChart;
    // mChart存放数据
    private final XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    // mChart画图用
    private XYSeriesRenderer mRenderer;
    private XYSeriesRenderer div10Renderer;
    private final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

    // 暂停
    private Button btPause;
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取提示textView
        promptTextView = findViewById(R.id.promptText);

        // 初始化Doppler
        doppler = new Doppler();

        // 暂停按钮设置
        btPause = findViewById(R.id.btPause);
        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPause.setSelected(!btPause.isSelected());
                if(btPause.isSelected()){
                    btPause.setText("ON");
                    isPause=false;
                    doppler.start();
                } else {
                    btPause.setText("PAUSE");
                    isPause=true;
                    doppler.pause();
                }
            }
        });

        renderGraph();
        startGraph();

    }

    public void startGraph() {
        // 图像数据
        doppler.setOnReadCallback(new Doppler.OnReadCallback() {
            @Override
            public void onBandwidthRead(int leftBandwidth, int rightBandwidth) {

            }
            @Override
            public void onBinsRead(double[] bins) {
                // 清空图表数据
                mSeries.clear();
                div10.clear();
                // bins[920]的值作为基准，画一条红色直线
                double frac = bins[929] * doppler.maxVolRatio;
                Log.d("PRIMARY VOL", bins[929] + "");
                // bins[1850]-bins[1865]，画一条蓝色折线
                for (int i = 1850; i < 1865; ++i) {
                    mSeries.add(i, bins[i]);
                    div10.add(i, frac);
                }
                mChart.repaint();
            }
        });

        // 检测手势
        doppler.setOnGestureListener(new Doppler.OnGestureListener() {
            @Override
            public void onPush() {
                promptTextView.setBackgroundColor(getResources().getColor(R.color.red));
                promptTextView.setText("push");
            }

            @Override
            public void onPull() {
                promptTextView.setBackgroundColor(getResources().getColor(R.color.blue));
                promptTextView.setText("pull");
            }

            @Override
            public void onTap() {
                promptTextView.setBackgroundColor(getResources().getColor(R.color.purple));
                promptTextView.setText("tap");
            }

            @Override
            public void onDoubleTap() {
                promptTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                promptTextView.setText("doubleTap");
            }

            @Override
            public void onNothing() {
                promptTextView.setBackgroundColor(getResources().getColor(R.color.neutral));
                promptTextView.setText("nothing");
            }
        });
    }

    private void renderGraph() {
        mSeries = new XYSeries("Vols/FreqBin");
        div10 = new XYSeries("div10");
        mSeries.add(3,4);
        mRenderer = new XYSeriesRenderer();
        div10Renderer = new XYSeriesRenderer();
        div10Renderer.setColor(getResources().getColor(R.color.red));
        dataset.addSeries(mSeries);
        dataset.addSeries(div10);
        renderer.addSeriesRenderer(mRenderer);
        renderer.addSeriesRenderer(div10Renderer);
        // 允许X轴可拉动
        renderer.setPanEnabled(true);
        // 允许缩放
        renderer.setZoomEnabled(true);
        renderer.setZoomEnabled(true);
        // 显示放大缩小按钮
        renderer.setZoomButtonsVisible(true);
        // 设置文字大小
        renderer.setLabelsTextSize(28);
        // 蓝色线具体设置
        mRenderer.setDisplayChartValues(true);
        mRenderer.setPointStyle(PointStyle.CIRCLE);
        mRenderer.setPointStrokeWidth(6);
        mRenderer.setChartValuesTextSize(28);
        mRenderer.setDisplayChartValuesDistance(10);


        mChart = ChartFactory.getLineChartView(MainActivity.this, dataset, renderer);

        ((LinearLayout)findViewById(R.id.chart)).addView(mChart);
    }
}
