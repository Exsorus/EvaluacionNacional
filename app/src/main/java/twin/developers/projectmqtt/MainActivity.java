package twin.developers.projectmqtt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    private Mqtt mqttManager;
    Button bttncalcular;
    EditText txtPeso;
    EditText txtNombre;
    EditText txtAltura;
    TextView txtResultado;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttncalcular = findViewById(R.id.bttn_Calcular);
        txtPeso = findViewById(R.id.txt_Peso);
        txtNombre = findViewById(R.id.txt_Nombre);
        txtAltura = findViewById(R.id.txt_altura);
        txtResultado = findViewById(R.id.txt_Resultado);

        mqttManager = new Mqtt(getApplicationContext());
        mqttManager.connectToMqttBroker();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        bttncalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pesoText = txtPeso.getText().toString();
                String alturaText = txtAltura.getText().toString();
                String nombreText = txtNombre.getText().toString();

                // Calcular el IMC
                double peso = Double.parseDouble(pesoText);
                double altura = Double.parseDouble(alturaText);
                double imc = peso / Math.pow((altura / 100), 2);

                int imcInt = (int) imc;

                String mensaje = nombreText + ", tu IMC es " + imcInt;

                mqttManager.publishMessage(mensaje);
                //MqttMessage message = new MqttMessage("IMC".getBytes());
                //mqttManager.publishMessage("IMC");


                // Almacenar el resultado en Firebase
                databaseReference.child("IMC").setValue(mensaje);

            }

        });
        databaseReference.child("IMC").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Recuperar el resultado desde Firebase
                String imc = snapshot.getValue(String.class);
                txtResultado.setText("IMC: " + imc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}