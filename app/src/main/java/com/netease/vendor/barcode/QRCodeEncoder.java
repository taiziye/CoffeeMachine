package com.netease.vendor.barcode;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import com.netease.vendor.util.ScreenUtil;
import com.netease.vendor.util.log.LogUtil;

import java.util.EnumMap;
import java.util.Map;

public class QRCodeEncoder {

	private static final int BLACK = 0xff000000;

	private static final int WHITE = 0xffffffff;

	public static Bitmap getQrCodeBitmap(Context context, String str, final int size) throws WriterException {
		return getQrCodeBitmap(context, str, size, BLACK, WHITE);
	}

	private static Bitmap getQrCodeBitmap(Context context, String str, final int size, final int fillColor, int oppositeColor)
			throws WriterException {
		Bitmap bitmap = null;
		Map<EncodeHintType, Object> hints = null;
		hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.MARGIN, 0);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		bitmap = createQRCode(str, ScreenUtil.dip2px(size), ScreenUtil.dip2px(size), hints, fillColor, oppositeColor);
		return bitmap;
	}

	private static Bitmap createQRCode(String str, int w, int h, Map<EncodeHintType, Object> hints, int fillColor,
			int oppositeColor) throws WriterException {
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, w, h, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = matrix.get(x, y) ? fillColor : oppositeColor;
			}
		}
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		} catch (OutOfMemoryError e) {
			LogUtil.e("createQRCode", e.getMessage());
		}

		return bitmap;
	}
}
