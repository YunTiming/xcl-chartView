package com.example.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xclcharts.chart.CustomLineData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.DrawHelper;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotGrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @Description 曲线图 的例子
 * @author XiongChuanLiang<br/>
 *         (xcl_168@aliyun.com)
 */
public class ChartView extends DemoView {

	private String TAG = "ChartView";
	private SplineChart chart = new SplineChart();
	// 分类轴标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
	Paint pToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);

	private List<CustomLineData> mCustomLineDataset = new LinkedList<CustomLineData>();
	private SplineData dataSeries1;

	public ChartView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public ChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		chartLabels();// 标签
		chartDataSet();
		chartDesireLines();
		chartRender();

		// 綁定手势滑动事件
		this.bindTouch(this, chart);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
	}

	private void chartRender() {
		try {
			chart.disableScale();// 不允许缩放
			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int[] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(
					// 分别是left,top,right,bottom
					// ltrb[0] + DensityUtil.dip2px(this.getContext(), 10),
					// ltrb[1],
					// ltrb[2] + DensityUtil.dip2px(this.getContext(), 20),
					// ltrb[3]);
					DensityUtil.dip2px(this.getContext(), 30),
					DensityUtil.dip2px(this.getContext(), 30),
					DensityUtil.dip2px(this.getContext(), 30),
					DensityUtil.dip2px(this.getContext(), 30));

			// 标题
			// chart.setTitle("New GitHub repositories");
			// chart.addSubtitle("(XCL-Charts Demo)");
			// chart.getAxisTitle().setLeftTitle("Percentage (annual)");
			// chart.getAxisTitle().getLeftTitlePaint().setColor(Color.BLACK);

			// 显示边框
			chart.showRoundBorder();

			// 数据源
			chart.setCategories(labels);
			chart.setDataSource(chartData);
			chart.setCustomLines(mCustomLineDataset);

			// 坐标系
			// 数据轴最大值
			chart.getDataAxis().setAxisMax(100);
			// chart.getDataAxis().setAxisMin(0);
			// 数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(10);

			// 标签轴最大值
			chart.setCategoryAxisMax(12);
			// 标签轴最小值
			chart.setCategoryAxisMin(1);

			// 背景网格
			PlotGrid plot = chart.getPlotGrid();
			plot.hideHorizontalLines();// 隐藏横向网格
			plot.hideVerticalLines();// 隐藏纵向网格

			/** --------------------Y轴-------------------------------- */
			chart.getDataAxis().getAxisPaint()
					.setColor(Color.rgb(12, 176, 155));// Y轴颜色
			chart.getDataAxis().getTickMarksPaint()
					.setColor(Color.rgb(12, 176, 155));// Y轴刻度尺的颜色
			// 刻度尺位于Y轴的右方
			chart.getDataAxis().setHorizontalTickAlign(Align.RIGHT);
			// 标签与刻度尺的距离
			chart.getDataAxis().setTickLabelMargin(-20);
			chart.getDataAxis().getTickLabelPaint()
					.setColor(Color.rgb(12, 176, 155));
			chart.getDataAxis().getTickLabelPaint().setFakeBoldText(false);// 设置文本仿粗体
			chart.getDataAxis().getTickLabelPaint().setTextSize(20);

			/** --------------------X轴-------------------------------- */
			chart.getCategoryAxis().getAxisPaint()
					.setColor(Color.rgb(12, 176, 155));// X轴颜色
			chart.getCategoryAxis().getTickMarksPaint()
					.setColor(Color.rgb(12, 176, 155));// X轴刻度尺的颜色
			chart.getCategoryAxis().getTickLabelPaint()
					.setColor(Color.rgb(12, 176, 155));
			chart.getCategoryAxis().getTickLabelPaint().setFakeBoldText(false);// 设置文本仿粗体
			chart.getCategoryAxis().getTickLabelPaint().setTextSize(20);
			chart.getCategoryAxis().setTickLabelMargin(20);
			// 刻度尺位于X轴的上方
			chart.getCategoryAxis().setVerticalTickPosition(
					XEnum.VerticalAlign.TOP);

			// 定义数据轴标签显示格式
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
					Double tmp = Double.parseDouble(value);
					DecimalFormat df = new DecimalFormat("#0");
					String label = df.format(tmp).toString();
					return (label);
				}
			});

			// 不使用精确计算，忽略Java计算误差,提高性能
			chart.disableHighPrecision();

			chart.disablePanMode();
			chart.hideBorder();
			chart.getPlotLegend().hide();

			// chart.getCategoryAxis().setLabelLineFeed(XEnum.LabelLineFeed.ODD_EVEN);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
	}

	private void chartDataSet() {

		// 线1的数据集
		List<PointD> linePoint1 = new ArrayList<PointD>();
		linePoint1.add(new PointD(1d, 3d));
		linePoint1.add(new PointD(2d, 0d));
		linePoint1.add(new PointD(3d, 6d));
		linePoint1.add(new PointD(4d, 0d));
		linePoint1.add(new PointD(5d, 9d));
		linePoint1.add(new PointD(6d, 0d));
		linePoint1.add(new PointD(7d, 12d));
		linePoint1.add(new PointD(8d, 0d));
		linePoint1.add(new PointD(9d, 15d));
		linePoint1.add(new PointD(10d, 0d));
		linePoint1.add(new PointD(11d, 18d));
		linePoint1.add(new PointD(12d, 0d));
		dataSeries1 = new SplineData("", linePoint1, Color.rgb(255, 0, 0));
		// 把线弄细点
		dataSeries1.getLinePaint().setStrokeWidth(2);// 线的宽度
		dataSeries1.setLineStyle(XEnum.LineStyle.DASH);
		dataSeries1.setLabelVisible(false);
		dataSeries1.setDotStyle(XEnum.DotStyle.HIDE);

		chartData.add(dataSeries1);

	}

	private void chartLabels() {
		// labels.add("2018");
		// labels.add("2019");
		// labels.add("2020");
		// labels.add("2021");
		// labels.add("2022");
		// labels.add("2023");
		for (int i = 1; i <= 12; i++) {
			if (i <= 9) {
				labels.add("0" + i + "月");
			} else {
				labels.add(i + "月");
			}
		}
	}

	private void chartDesireLines() {
		CustomLineData s = new CustomLineData("", 15d, Color.rgb(54, 141, 238),
				3);

		s.hideLine();
		s.getLineLabelPaint().setColor(Color.rgb(54, 141, 238));
		s.getLineLabelPaint().setTextSize(27);
		s.setLineStyle(XEnum.LineStyle.DASH);
		s.setLabelOffset(5);
		mCustomLineDataset.add(s);

	}

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private float textHeight;
	private float textWidth;
	String titleX;
	String titleY;

	@Override
	public void render(Canvas canvas) {
		try {
			chart.render(canvas);
			paint.setTextSize(35);
			paint.setColor(Color.RED);

			textHeight = DrawHelper.getInstance().getPaintFontHeight(paint);
			textWidth = DrawHelper.getInstance().getTextWidth(paint, "XXX");

			paint.setTextAlign(Align.LEFT);
			canvas.drawText(titleY, chart.getPlotArea().getLeft() - textWidth,
					chart.getPlotArea().getTop() - textHeight, paint);

			paint.setTextAlign(Align.RIGHT);
			canvas.drawText(titleX, chart.getPlotArea().getRight() + textWidth,
					chart.getPlotArea().getBottom() + 2 * textHeight, paint);

		} catch (Exception e) {
		}
	}

	// ---------------------------------------对外接口-------------
	/**
	 * 更改线的数据
	 * 
	 * @demo List<PointD> linePoint1 = new ArrayList<PointD>();
	 *       linePoint1.add(new PointD(1d, 3d)); ......
	 * @param linePoint1
	 */
	public void refreshChart(List<PointD> linePoint1) {
		dataSeries1.setLineDataSet(linePoint1);
		this.invalidate();
	}

	/**
	 * 更改线和横坐标的数据
	 * 
	 * @param linePoint1
	 * @param labels1
	 * @demo LinkedList<String> labels1 = new LinkedList<String>();
	 *       labels1.add("1");.....
	 */
	public void refreshChart(List<PointD> linePoint1, LinkedList<String> labels1) {
		dataSeries1.setLineDataSet(linePoint1);
		chart.setCategories(labels1);
		this.invalidate();
	}

	/**
	 * 更改线，横坐标，纵坐标的数据
	 * 
	 * @param linePoint1
	 * @param labels1
	 * @param max
	 *            纵坐标最大值
	 * @param step
	 *            纵坐标刻度间隔值
	 */
	public void refreshChart(List<PointD> linePoint1,
			LinkedList<String> labels1, double max, double step) {
		dataSeries1.setLineDataSet(linePoint1);
		chart.setCategories(labels1);
		chart.getDataAxis().setAxisMax(max);
		chart.getDataAxis().setAxisSteps(step);
		this.invalidate();
	}

	public void refreshChart(List<PointD> linePoint1, double max, double step) {
		dataSeries1.setLineDataSet(linePoint1);
		chart.getDataAxis().setAxisMax(max);
		chart.getDataAxis().setAxisSteps(step);
		this.invalidate();
	}

	public void setText(String X, String Y) {
		this.titleX = X;
		this.titleY = Y;

	}
}