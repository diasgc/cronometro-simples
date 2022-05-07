package pt.gu.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import pt.gu.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements Runnable {

    private ActivityMainBinding binding;

    private boolean chronoIsRunning = false;
    private long chronoTimeStepMiliseconds = 10L;
    private long startTime = 0L;

    private Button button;
    private TextView chrono;

    private Handler mHandler;

    // constante de formato do tempo: Horas:Minutos:Segundos.Milissegundos
    private final Calendar CALENDAR = Calendar.getInstance(TimeZone.getDefault());
    private final SimpleDateFormat CHRONO_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        chrono = findViewById(R.id.chronometer);
        button = findViewById(R.id.startStopButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doStartStop();
            }
        });
        CHRONO_FORMAT.setTimeZone(TimeZone.getTimeZone("utc"));
        mHandler = new Handler(getMainLooper());
    }

    // implementação da interface Runnable
    // Thread paralela responsavel por executar o cronometro cada 10 milisegundos
    // definidos pela variavel chronoTimeStepMiliseconds

    @Override
    public void run() {
        // actualizar a caixa de texto não pode ser na thread paralela, tem que ser na thread do programa principal.
        // senão dá erro

        // To do: o cronómetro mostra hora inicial '01' por causa do fuso horário.
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chrono.setText(CHRONO_FORMAT.format(System.currentTimeMillis() - startTime));
            }
        });
        // repetir após 10 milisegundos:
        mHandler.postDelayed(this,chronoTimeStepMiliseconds);
    }

    private void doStartStop(){
        if (chronoIsRunning){
            // parar a thread paralela
            mHandler.removeCallbacks(this);
            chronoIsRunning = false;
            button.setText("start");
        } else {
            // começar: tempo inicial é 'Agora' em milissegundos
            startTime = System.currentTimeMillis();
            // criar nova thread paralela e executá-la
            mHandler.post(this);
            button.setText("stop");
            chronoIsRunning = true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}