package pl.pw.mierzopuls.alg

import android.util.Log
import org.opencv.android.OpenCVLoader

class ImageProcessing {
    init {
        if (!OpenCVLoader.initDebug()) {
            Log.e("ImgProc", "Unable to load OpenCV! BE")
            throw InstantiationException("OpenCV not loaded correctly!")
        } else {
            Log.d("ImgProc, ","OpenCV library loaded correctly")
        }
    }
    companion object {

    }
}

