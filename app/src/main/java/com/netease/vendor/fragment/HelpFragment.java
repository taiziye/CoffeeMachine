package com.netease.vendor.fragment;

import com.netease.vendor.R;
import com.netease.vendor.common.fragment.TFragment;
import com.netease.vendor.service.Remote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HelpFragment extends TFragment implements OnClickListener{
    
	private ImageView mBackBtn;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
    
    private void initView(){
    	mBackBtn = (ImageView) getView().findViewById(R.id.help_back_btn);
    	mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buy_coffee_back_btn:
    			this.getActivity().finish();
    			break;
            default:
                 break;
        }
    }

	@Override
	public void onReceive(Remote remote) {
	}
}
