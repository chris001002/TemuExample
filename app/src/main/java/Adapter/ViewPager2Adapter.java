package Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.Cart;
import Fragments.Orders;
import Fragments.Products;

public class ViewPager2Adapter extends FragmentStateAdapter {
    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Products();
            case 1:
                return new Cart();
            case 2:
                return new Orders();
            default:
                return new Products();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
