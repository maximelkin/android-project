package ru.ifmo.droid2016.lineball;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    private HelpAdapter adapter;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().hide();
        adapter = new HelpAdapter(this);
        viewpager = (ViewPager)findViewById(R.id.pager);
        viewpager.setAdapter(adapter);
    }

    private class HelpAdapter extends PagerAdapter {

        int[] images = {
                R.drawable.help01,
                R.drawable.help02,
                R.drawable.help03,
                R.drawable.help04,
                R.drawable.help05,
                R.drawable.help06
        };

        int[] strings = {
                R.string.help01,
                R.string.help02,
                R.string.help03,
                R.string.help04,
                R.string.help05,
                R.string.help06
        };

        Context context;
        LayoutInflater layoutInflater;

        HelpAdapter(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = layoutInflater.inflate(R.layout.item_help, container, false);
            ImageView image = (ImageView) itemView.findViewById(R.id.image_help);
            TextView text = (TextView) itemView.findViewById(R.id.text_help);

            image.setImageBitmap(BitmapFactory.decodeResource(getResources(), images[position]));
            text.setText(strings[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View)object;
            container.removeView(view);
            view = null;
        }

    }

}