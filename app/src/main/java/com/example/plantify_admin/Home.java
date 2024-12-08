package com.example.plantify_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.plantify_admin.add_product.add_product;
import com.example.plantify_admin.chats.chats;
import com.example.plantify_admin.feedbacks.Feedbacks;
import com.example.plantify_admin.forDelivery.for_delivery;
import com.example.plantify_admin.orderDeliveries.Deliveries;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    private ImageView ImageMenu;
    private NavigationView nav;
    private ImageView imagebutton;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        nav = findViewById(R.id.home_menu);
        drawer = findViewById(R.id.drawer);
        imagebutton = findViewById(R.id.imageButton);

        setFragment(new home_layout());

        imagebutton.setOnClickListener(v -> {
            drawer.open();
        });

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemid = menuItem.getItemId();

                if (itemid == R.id.Home) {
                    setFragment(new home_layout());
                    Toast.makeText(Home.this, "Home", Toast.LENGTH_SHORT).show();
                }
                if (itemid == R.id.Add_Product) {
                    setFragment(new add_product());
                    Toast.makeText(Home.this, "Add Products", Toast.LENGTH_SHORT).show();
                }
                if (itemid == R.id.Orders) {
                    setFragment(new Deliveries());
                }
                if (itemid == R.id.chats) {
                    setFragment(new chats());
                }
                if (itemid == R.id.Delivery) {
                    setFragment(new for_delivery());
                }
                if (itemid == R.id.FeedBacks) {
                    setFragment(new Feedbacks());
                }
                if (itemid == R.id.Sign_Out) {
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    firebaseAuth.signOut(); // Clear Firebase session
                    startActivity(intent);
                    finish(); // Close Home activity
                }

                // Close the navigation drawer after item selection
                drawer.closeDrawer(nav);

                return true; // Indicate that the item was handled
            }
        });
    }


    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main,fragment);
        fragmentTransaction.commit();

    }
}