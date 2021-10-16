package pl.pw.mierzopuls.model

import org.bytedeco.javacv.FrameGrabber

class Camera() {
    private var grabber: FrameGrabber = FrameGrabber.createDefault(0)
}