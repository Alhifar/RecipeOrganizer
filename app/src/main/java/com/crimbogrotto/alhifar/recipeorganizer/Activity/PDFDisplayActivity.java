package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = 0;
        int height = 0;
        int pageCount = 0;

        //File dir = Environment.getExternalStorageDirectory();
        //File pdf = new File(dir, "test.pdf");
        String id = getIntent().getStringExtra("id");
        File pdf = getApplicationContext().getFileStreamPath("test"+id+".pdf");

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
                ((ImageView) findViewById(R.id.pdf)).setImageBitmap(pageImage);

                //Immediately scroll image to top of page
                ((ImageView) findViewById(R.id.pdf)).scrollBy(0,-1 * (int)(((height * pageCount) / 2) - (size.y / 2)));
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

        // set maximum scroll amount (based on center of image)
        int maxY = (int)(((height * pageCount) / 2) - (size.y / 2));

        // set scroll limits
        final int maxTop = 0;
        final int maxBottom = maxY * 2;

        final ImageView scrollingImageView = (ImageView) this.findViewById(R.id.pdf);

        scrollingImageView.setOnTouchListener(new View.OnTouchListener()
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

                        scrollingImageView.scrollBy(scrollByX, scrollByY);
                        downY = currentY;
                        break;

                }

                return true;
            }
        });
    }
}
