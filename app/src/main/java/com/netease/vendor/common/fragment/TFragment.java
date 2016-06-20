package com.netease.vendor.common.fragment;

import com.netease.vendor.common.action.RemoteProxy;
import com.netease.vendor.common.action.TViewWatcher;
import com.netease.vendor.inter.IServiceBindListener;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.VendorService;
import com.netease.vendor.util.log.LogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

public abstract class TFragment extends Fragment {
	private static final Handler handler = new Handler();
	
	private int fragmentId;
	
	private boolean destroyed;
	
	protected final boolean isDestroyed() {
		return destroyed;
	}

	public int getFragmentId() {
		return fragmentId;
	}

	public void setFragmentId(int fragmentId) {
		this.fragmentId = fragmentId;
		
	}
 
	public abstract void onReceive(Remote remote);
	
    private RemoteProxy proxy = new RemoteProxy() {
		@Override
		public void onReceive(Remote remote) {
			TFragment.this.onReceive(remote);
		}
	};
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LogUtil.vendor("fragment: " + getClass().getSimpleName() + " onActivityCreated()");

		destroyed = false;

		proxy.bind(true);
	}

	public void onDestroy() {
		super.onDestroy();

		LogUtil.vendor("fragment: " + getClass().getSimpleName() + " onDestroy()");
		
		destroyed = true;
		
		proxy.bind(false);
	}
	
	public Remote execute(Remote remote) {
		TViewWatcher.newInstance().execute(remote);
		return remote;
	}

	public Remote executeBackground(Remote remote) {
		TViewWatcher.newInstance().executeBackground(remote);
		return remote;
	}
	
	protected final Handler getHandler() {
		return handler;
	}
	
	protected final void postRunnable(final Runnable runnable) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				// validate
				// TODO use getActivity ?
				if (!isAdded()) {
					return;
				}
				
				// run
				runnable.run();
			}
		});
	}
	
	protected final void postDelayed(final Runnable runnable, long delay) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// validate
				// TODO use getActivity ?
				if (!isAdded()) {
					return;
				}
				
				// run
				runnable.run();
			}
		}, delay);
	}

	/**
     * is bound
     *
     * @return bind state
     */
    protected boolean isBound() {
    	return VendorService.isBind;
    }

    /**
     * request bind
     */
    protected void requestBind() {
    	if(isBound()) {
			// handle bound
			handleBound();
		} else {
			// listen bind
			TViewWatcher.newInstance().addServiceBinderListener(mServiceBindListener);
		}
    }

    /**
     * handle bound
     */
    protected void handleBound() {

    }

    /**
     * service bind listener
     */
    protected IServiceBindListener mServiceBindListener = new IServiceBindListener() {
    	@Override
		public void onBindSuccess() {
			// handle bound
			handleBound();
		}

		@Override
		public void onBindFailed(String errorMessage) {
			// TODO
		}
    };

}
