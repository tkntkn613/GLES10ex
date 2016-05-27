package jp.ac.titech.itpro.sdl.gles10ex;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SensorEventListener {
    private final static String TAG = "MainActivity";

    private GLSurfaceView glView;
    private SimpleRenderer renderer;
    private SeekBar rotationBarX, rotationBarY, rotationBarZ;

    private SensorManager sensorMgr;
    private Sensor accel;
    private final static float alpha = 0.8f;
    private float vx, vy, vz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        glView = (GLSurfaceView) findViewById(R.id.glview);

        rotationBarX = (SeekBar) findViewById(R.id.rotation_bar_x);
        rotationBarY = (SeekBar) findViewById(R.id.rotation_bar_y);
        rotationBarZ = (SeekBar) findViewById(R.id.rotation_bar_z);
        rotationBarX.setOnSeekBarChangeListener(this);
        rotationBarY.setOnSeekBarChangeListener(this);
        rotationBarZ.setOnSeekBarChangeListener(this);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accel == null) {
            Toast.makeText(this, getString(R.string.toast_no_accel_error),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        renderer = new SimpleRenderer();
        renderer.addObj(new Cube(0.5f, 0, 0.2f, -3));
        renderer.addObj(new Pyramid(0.5f, 0, 0, 0));
        glView.setRenderer(renderer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        sensorMgr.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        glView.onPause();
        sensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 加速度 ==> 正規化して-1.0 ~ 1.0 ==> 0 ~ 360に変換
        // a ¥in [-1.0~1.0] ==> (a + 1.0) * 180
        float ax = event.values[0], ay = event.values[1], az = event.values[2];
        vx = alpha * vx + (1 - alpha) * ax;
        vy = alpha * vy + (1 - alpha) * ay;
        vz = alpha * vz + (1 - alpha) * az;
        Log.d(TAG, "(" + vx + ", " + vy + ", " + vz + ")");
        /*
        double vs = Math.sqrt(vx*vx + vy*vy + vz*vz);
        rotationBarX.setProgress((int)(180.0 * (vx/vs + 1.0)));
        rotationBarY.setProgress((int)(180.0 * (vy/vs + 1.0)));
        rotationBarZ.setProgress((int)(180.0 * (vz/vs + 1.0)));
        */
        double theta = Math.atan2(-vx, vy);
        double phi   = Math.atan2(vz, vy);
        //double roll = Math.atan2(vz, vx);
        //double pitch = Math.atan2(vy, vz);
        //double yaw = Math.atan2(vy, vx);
        rotationBarX.setProgress(((int)(phi*180/Math.PI) + 360) % 360);
        rotationBarZ.setProgress(((int)(theta*180/Math.PI) + 360) % 360);
        //rotationBarX.setProgress((int)(-pitch*180/Math.PI) + 180);
        //rotationBarY.setProgress((int)(roll*180/Math.PI) + 180);
        //rotationBarZ.setProgress(((int)(yaw*180/Math.PI) + 270) % 360);
        //Log.d(TAG, "(" + roll + ", " + pitch + ", " + yaw + ")");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == rotationBarX)
            renderer.setRotationX(progress);
        else if (seekBar == rotationBarY)
            renderer.setRotationY(progress);
        else if (seekBar == rotationBarZ)
            renderer.setRotationZ(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
