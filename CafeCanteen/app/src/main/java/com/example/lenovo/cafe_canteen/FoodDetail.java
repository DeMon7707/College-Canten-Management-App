package com.example.lenovo.cafe_canteen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.lenovo.cafe_canteen.Common.Common;
import com.example.lenovo.cafe_canteen.Database.Database;
import com.example.lenovo.cafe_canteen.Model.Food;
import com.example.lenovo.cafe_canteen.Model.Order;
import com.example.lenovo.cafe_canteen.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;

    Button btnShowComment;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        btnShowComment = (Button)findViewById(R.id.btnShowComment);
        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodDetail.this,ShowComment.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(intent);
            }
        });

        //Firebase

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTbl = database.getReference("Rating");

        //init view

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton) findViewById(R.id.btn_rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        food_description = (TextView) findViewById(R.id.food_description);
        food_price = (TextView) findViewById(R.id.food_price);
        food_name = (TextView) findViewById(R.id.food_name);
        food_image = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        //Get foodid from intent

        if(getIntent() !=null)
        {       foodId = getIntent().getStringExtra("FoodId");
        }
        if(!foodId.isEmpty())
        {
            if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRatingFood(foodId);
            }
            else
            {
                Toast.makeText(this, "Please check your Internet Connection !!!", Toast.LENGTH_SHORT).show();
            }

        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getName(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount()

                    ));

                    Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(FoodDetail.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });



    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if(count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Good","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Food")
                .setDescription("Please select some stars and give your Feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {


        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentFood = dataSnapshot.getValue(Food.class);

                //Set image
                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
        //Get rating and upload to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments);

        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "Thanks for your Feedback !!!", Toast.LENGTH_SHORT).show();
                    }
                });
        /*
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                    //Remove old value (Can delete or let it be, No difference)
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //Set new value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else {
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this, "Thanks for your Feedback !!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });*/
    }
}
