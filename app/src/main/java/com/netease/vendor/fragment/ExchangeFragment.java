package com.netease.vendor.fragment;

import com.netease.vendor.R;
import com.netease.vendor.common.fragment.TFragment;
import com.netease.vendor.service.Remote;
import com.netease.vendor.ui.DigitsEditText;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ExchangeFragment extends TFragment implements OnClickListener{
    
	private ImageView mBackBtn;
	private DigitsEditText mEditText;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exchange, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
    
    private void initView(){
    	mBackBtn = (ImageView) getView().findViewById(R.id.fetch_back_btn);
    	mBackBtn.setOnClickListener(this);
    	
    	initEditText();
    	initDigitsPad();
    }
    
    private void initEditText(){
    	mEditText = (DigitsEditText) getView().findViewById(R.id.exchange_input_edit);
    }
    
    private void initDigitsPad(){
        getView().findViewById(R.id.keyboard_number1).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number2).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number3).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_back).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number4).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number5).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number6).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_clear).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number7).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number8).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number9).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_star).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number0).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_pound).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_sure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.keyboard_number1:
                 keyPressed(KeyEvent.KEYCODE_1);
                 break;
            case R.id.keyboard_number2:
                 keyPressed(KeyEvent.KEYCODE_2);
                 break;
            case R.id.keyboard_number3:
                 keyPressed(KeyEvent.KEYCODE_3);
                 break;
            case R.id.keyboard_number4:
                 keyPressed(KeyEvent.KEYCODE_4);
                 break;
            case R.id.keyboard_number5:
                 keyPressed(KeyEvent.KEYCODE_5);
                 break;
            case R.id.keyboard_number6:
                 keyPressed(KeyEvent.KEYCODE_6);
                 break;
            case R.id.keyboard_number7:
                 keyPressed(KeyEvent.KEYCODE_7);
                 break;
            case R.id.keyboard_number8:
                 keyPressed(KeyEvent.KEYCODE_8);
                 break;
            case R.id.keyboard_number9:
                 keyPressed(KeyEvent.KEYCODE_9);
                 break;
            case R.id.keyboard_number0:
                 keyPressed(KeyEvent.KEYCODE_0);
                 break;
            case R.id.keyboard_back:
                 onDelete();
                 break;
            case R.id.keyboard_clear:
            	onDeleteAll();
                break;
            case R.id.keyboard_star:
            	keyPressed(KeyEvent.KEYCODE_STAR);
            	break;
            case R.id.keyboard_pound:
            	keyPressed(KeyEvent.KEYCODE_POUND);
                break;
            case R.id.keyboard_sure:
            	doExchangeCoffee();
                break;
            case R.id.buy_coffee_back_btn:
    			this.getActivity().finish();
    			break;
            default:
                 break;
        }
    }
    
    private void keyPressed(int keyCode) {
         KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
         mEditText.onKeyDown(keyCode, event);
    }
    
    private void onDeleteAll(){
        final Editable digits = mEditText.getText();
        if(digits != null)
            digits.clear();
    }
    
    private void onDelete(){
        keyPressed(KeyEvent.KEYCODE_DEL);
    }
    
    private void doExchangeCoffee(){
    	
    }

	@Override
	public void onReceive(Remote remote) {
		
		
	}
   
}
