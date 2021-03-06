/*******************************************************************************
 * Gaggle is Copyright 2010 by Geeksville Industries LLC, a California limited liability corporation. 
 * 
 * Gaggle is distributed under a dual license.  We've chosen this approach because within Gaggle we've used a number
 * of components that Geeksville Industries LLC might reuse for commercial products.  Gaggle can be distributed under
 * either of the two licenses listed below.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * Commercial Distribution License
 * If you would like to distribute Gaggle (or portions thereof) under a license other than 
 * the "GNU General Public License, version 2", contact Geeksville Industries.  Geeksville Industries reserves
 * the right to release Gaggle source code under a commercial license of its choice.
 * 
 * GNU Public License, version 2
 * All other distribution of Gaggle must conform to the terms of the GNU Public License, version 2.  The full
 * text of this license is included in the Gaggle source, see assets/manual/gpl-2.0.txt.
 ******************************************************************************/
package com.geeksville.info;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import com.geeksville.location.AccelerometerClient;

/**
 * Read the magnetic heading as measured by the accel
 * 
 * @author kevinh
 * 
 */
public class InfoGMeter extends InfoField implements Observer {

	private float g = 1.0f, gMin = 1.0f, gMax = 1.0f;

	private AccelerometerClient accel;

	@Override
	public String getLabel() {
		return "G";
	}

	/**
	 * 
	 * @see com.geeksville.info.InfoField#getText()
	 */
	@Override
	public String getText() {
		return String.format("%.1f/%.1f/%.1f", g, gMin, gMax);
	}

	/**
	 * 
	 * @see com.geeksville.info.InfoField#getUnits()
	 */
	@Override
	public String getUnits() {
		return "cur/min/max";
	}

	/**
	 * @see com.geeksville.info.InfoField#onCreate(android.app.Activity)
	 */
	@Override
	public void onCreate(Activity context) {
		super.onCreate(context);

		if (context != null) {

			// FIXME - we should share one accel client object
			accel = new AccelerometerClient(context);
		}
	}

	/**
	 * @see com.geeksville.info.InfoField#onHidden()
	 */
	@Override
	void onHidden() {
		super.onHidden();

		if (accel != null)
			accel.deleteObserver(this);
	}

	/**
	 * @see com.geeksville.info.InfoField#onShown()
	 */
	@Override
	void onShown() {
		super.onShown();

		if (accel != null)
			accel.addObserver(this);
	}

	@Override
	public void update(Observable observable, Object data) {

		// convert from m/sec to g's
		float newg = ((Float) data) / 9.6f;

		// if the phone is being pulled away from the pilot that reports as neg
		// g's but in pilot speak that is really positive gs
		newg = (newg + 1) + 1;

		if (newg != g) {
			g = newg;

			gMin = Math.min(g, gMin);
			gMax = Math.max(g, gMax);

			onChanged();
		}
	}

}
