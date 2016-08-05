package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crimbogrotto.alhifar.recipeorganizer.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Alhifar on 8/1/2016.
 */
public class PDFDisplayActivity extends AppCompatActivity {

    public Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth();
            height = c.getHeight() + s.getHeight();
        } else {
            width = s.getWidth();
            height = c.getHeight() + s.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);
        return cs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_display);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap finalPageImage = generateImageFromPdf();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setImage(finalPageImage);
                        ViewGroup layout = (ViewGroup)findViewById(R.id.pdf_layout);
                        layout.removeView(findViewById(R.id.text_dot_loader));
                        layout.invalidate();
                    }
                });
            }
        }).start();
        // Doesn't seem to be automatically running gc at
        // a reasonable time to clean up after all the bitmaps
        Runtime.getRuntime().gc();
    }

    private void setImage(Bitmap finalPageImage)
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        final ImageView pdfView = ((ImageView) findViewById(R.id.pdf));

        pdfView.setMinimumHeight(size.y);
        pdfView.setImageBitmap(finalPageImage);


        //Immediately scroll image to top of page
        pdfView.scrollBy(0,-1 * (int)((finalPageImage.getHeight() / 2) - (size.y / 2)));

        // set maximum scroll amount (based on center of image)
        int maxY = (int)((finalPageImage.getHeight() / 2) - (size.y / 2));

        // set scroll limits
        final int maxTop = 0;
        final int maxBottom = (maxY * 2) + size.y;

        pdfView.setOnTouchListener(new View.OnTouchListener()
        {
            float downX, downY;
            int totalX, totalY;
            int scrollByX, scrollByY;
            public boolean onTouch(View view, MotionEvent event)
            {
                float currentX, currentY;
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        downY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        currentY = event.getY();
                        scrollByX = 0;
                        scrollByY = (int)(downY - currentY);

                        // scrolling to top of image (pic moving to the bottom)
                        if (currentY > downY)
                        {
                            if (totalY == maxTop)
                            {
                                scrollByY = 0;
                            }
                            if (totalY > maxTop)
                            {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY < maxTop)
                            {
                                scrollByY = maxTop - (totalY - scrollByY);
                                totalY = maxTop;
                            }
                        }

                        // scrolling to bottom of image (pic moving to the top)
                        if (currentY < downY)
                        {
                            if (totalY == maxBottom)
                            {
                                scrollByY = 0;
                            }
                            if (totalY < maxBottom)
                            {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY > maxBottom)
                            {
                                scrollByY = maxBottom - (totalY - scrollByY);
                                totalY = maxBottom;
                            }
                        }

                        pdfView.scrollBy(scrollByX, scrollByY);
                        downY = currentY;
                        break;

                }

                return true;
            }
        });
    }

    private Bitmap generateImageFromPdf()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = 0;
        int height = 0;
        int pageCount = 0;
        Bitmap finalImage = null;
        String filename = getIntent().getStringExtra("filename");
        File pdf = getApplicationContext().getFileStreamPath(filename);

        PdfRenderer pdfRenderer = null;
        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdf, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);

            if (pdfRenderer != null)
            {
                pageCount = pdfRenderer.getPageCount();

                PdfRenderer.Page page = pdfRenderer.openPage(0);

                width = size.x;
                height = (int)Math.floor(((float)width / page.getWidth()) * page.getHeight());

                Bitmap pageImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Bitmap pageImage2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(pageImage, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                for (int i=1;i < pageCount;i++)
                {
                    page = pdfRenderer.openPage(i);
                    page.render(pageImage2, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    pageImage = combineImages(pageImage,pageImage2);
                    page.close();
                }

                finalImage = trimWhitespace(pageImage);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (pdfRenderer != null)
            {
                pdfRenderer.close();
            }
        }
        return finalImage;
    }

    private Bitmap trimWhitespace(Bitmap bm)
    {
        Boolean allWhiteRow;
        int allWhiteRowCount = 0;
        Bitmap finalBm = null;
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        int[] newBm = new int[bmHeight*bmWidth];
        //int[] pixels = new int[bmHeight*bmWidth];
        //bm.getPixels(pixels, 0, bmWidth, 0, 0, bmWidth, bmHeight);
        int offset = 0;

        int n = 0;

        for (int y=100;y<bm.getHeight();y++) //start at 100 to avoid trimming top whitespace
        {
            allWhiteRow = true;
            for (int x=0;x<bm.getWidth();x++)
            {
                int currentPixel = bm.getPixel(x,y);
                if (currentPixel != Color.WHITE && currentPixel != Color.TRANSPARENT )
                {
                    allWhiteRow = false;
                    allWhiteRowCount = 0;
                    break;
                }
            }
            /*for (int x=0;x<bmWidth;x++)
            {
                int currentPixel = ((y + offset) * bmWidth) + x;
                if (pixels[currentPixel] != Color.WHITE && pixels[currentPixel] != Color.TRANSPARENT )
                {
                    allWhiteRow = false;
                    allWhiteRowCount = 0;
                    break;
                }
                else
                {
                    Log.d("pixelChecker", "nonwhite");
                }
            }*/
            if (allWhiteRow)
            {
                allWhiteRowCount += 1;
            }
            if (allWhiteRowCount == 25)
            {
                int oldHeight = bm.getHeight();
                int newSize = bmWidth * (oldHeight - 24);
                //newBm = new int[newSize];
                bm.getPixels(newBm, 0, bmWidth, 0, 0, bmWidth, y - 25);
                bm.getPixels(newBm, bmWidth * (y-25), bmWidth, 0, y, bmWidth, oldHeight - y);
                //bm.getPixels(newBm, 0, oldWidth, 0, 0, oldWidth, y - 25 + offset);
                //bm.getPixels(newBm, oldWidth * ( y - 25 + offset), bm.getWidth(), 0, y, oldWidth, oldHeight - y);
/*                for (int i=0;i<bmWidth;i++) {
                    newBm[(bmWidth*y)+i]=Color.BLACK;
                }*/
                Runtime.getRuntime().gc(); // Android doesn't attempt to gc if out of memory for bitmaps
                bm = Bitmap.createBitmap(newBm, bmWidth, oldHeight - 25, Bitmap.Config.ARGB_8888);
                allWhiteRowCount = 0;
                //offset += 25;
                y -= 25;
                n++;
            }
        }
        newBm=null;
        return bm;
    }
}
