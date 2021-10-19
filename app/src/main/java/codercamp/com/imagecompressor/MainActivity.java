package codercamp.com.imagecompressor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {
    private EditText height, width;
    private SeekBar seekBarQuality;
    private TextView TextOriginal, TextCompressed, TextQuality;
    private Button BtnPicImage, BtnCompressedImage;
    private ImageView OriginalImageView, CompressedImageView;
    private static String filePath;
    public static final int RequestCode = 2;
    public File originalFile, compressedFile;
    private final File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "Image Compressor");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AskPermission();
        initView();
        PicImage();
        CompressedImage();

        filePath = path.getAbsolutePath();

        if (!path.exists()) {
            path.mkdirs();
        }
    }

    private void CompressedImage() {
        BtnCompressedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mHeight = height.getText().toString();
                String mWidth = width.getText().toString();

                if (mHeight.isEmpty()){
                    height.setError("Please Enter Height");
                    height.requestFocus();
                }
                if (mWidth.isEmpty()){
                    width.setError("Please Enter Width");
                    width.requestFocus();
                }
                int Quality = seekBarQuality.getProgress();
                int Height = Integer.parseInt(mHeight);
                int Width = Integer.parseInt(mWidth);
                try {
                    compressedFile = new Compressor(MainActivity.this)
                            .setMaxHeight(Height)
                            .setMaxWidth(Width)
                            .setQuality(Quality)
                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                            .setDestinationDirectoryPath(filePath)
                            .compressToFile(originalFile);
                    File finalFile = new File(filePath, originalFile.getName());
                    Bitmap finalBit = BitmapFactory.decodeFile(finalFile.getAbsolutePath());
                    CompressedImageView.setImageBitmap(finalBit);
                    TextCompressed.setText("Size: " + Formatter.formatShortFileSize(MainActivity.this, finalFile.length()));
                    Toast.makeText(MainActivity.this, "Image Compressed & Saved", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error While Compressing!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void PicImage() {
        BtnPicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, RequestCode);

            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            BtnCompressedImage.setVisibility(View.VISIBLE);
            final Uri uri = data.getData();
            try {
                final InputStream inputStream = getContentResolver().openInputStream(uri);
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                OriginalImageView.setImageBitmap(bitmap);
                originalFile = new File(uri.getPath().replace("raw/", ""));
                TextOriginal.setText("Size :" + Formatter.formatShortFileSize(this, originalFile.length()));
                Log.d("File" , String.valueOf(originalFile.length()));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "No Image Selected!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void AskPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void initView() {
        height = findViewById(R.id.Edit_Height);
        width = findViewById(R.id.Edit_Width);
        seekBarQuality = findViewById(R.id.SeekBarQuality);
        TextOriginal = findViewById(R.id.TextOriginal);
        TextCompressed = findViewById(R.id.TextCompressor);
        TextQuality = findViewById(R.id.TextQuality);
        BtnPicImage = findViewById(R.id.PicImageBtn);
        BtnCompressedImage = findViewById(R.id.CompressedImageBtn);
        OriginalImageView = findViewById(R.id.OriginalImage);
        CompressedImageView = findViewById(R.id.CompressorImage);



        seekBarQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextQuality.setText("Quality : " + i);
                seekBar.setMax(100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}