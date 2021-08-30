//package com.palashmax
//
//import javafx.application.Application
//import javafx.scene.Scene
//import javafx.scene.image.Image
//import javafx.scene.image.ImageView
//import javafx.scene.layout.HBox
//import javafx.stage.Stage
//import nu.pattern.OpenCV
//import org.opencv.core.*
//import org.opencv.imgcodecs.Imgcodecs
//import org.opencv.imgproc.Imgproc
//import org.opencv.objdetect.CascadeClassifier
//import org.opencv.objdetect.Objdetect
//import org.opencv.videoio.VideoCapture
//import java.io.ByteArrayInputStream
//import java.util.concurrent.Executors
//import java.util.concurrent.TimeUnit
//
//
//class FaceDetection: Application() {
//	private var FRAMES_PER_SECOND = 60
//
//	private fun loadImage(imagePath: String): Mat {
//		return Imgcodecs.imread(imagePath)
//	}
//
//	private fun saveImage(imageMatrix: Mat, targetPath: String) {
//		Imgcodecs.imwrite(targetPath, imageMatrix)
//	}
//
//	private fun detectFace(loadedImage: Mat): Mat {
//		val cascadeClassifier = CascadeClassifier()
//		cascadeClassifier.load(FaceDetection::class.java.getResource("haarcascase_frontalface_alt.xml")?.path)
//
//
//		val minFaceSize = loadedImage.rows() * 0.1
//		val facesDetected = MatOfRect()
//		cascadeClassifier.detectMultiScale(
//			loadedImage,
//			facesDetected,
//			1.1,
//			3,
//			Objdetect.CASCADE_SCALE_IMAGE,
//			Size(minFaceSize, minFaceSize),
//			Size()
//		)
//		val facesArray = facesDetected.toArray()
//		for(face in facesArray) {
//			Imgproc.rectangle(loadedImage, face.tl(), face.br(), Scalar(0.0, 255.0, 255.0), 3)
//		}
//		return loadedImage
//	}
//
//	private fun mat2Img(mat: Mat): Image {
//		val bytes = MatOfByte()
//		Imgcodecs.imencode("img", mat, bytes)
//		return Image(ByteArrayInputStream(bytes.toArray()))
//	}
//
//	private fun loadCamera(stage: Stage) {
//		OpenCV.loadShared()
//		// val capture = VideoCapture()
//		val imageView = ImageView()
//		val hBox = HBox(imageView)
//		val scene = Scene(hBox)
//		stage.scene = scene
//		stage.show()
//		/*object : AnimationTimer() {
//			override fun handle(l: Long) {
//				imageView.image = getCaptureWithFaceDetection()
//			}
//		}.start()*/
//		val frameGrabber = Runnable { getCaptureWithFaceDetection() }
//		val timer = Executors.newSingleThreadScheduledExecutor()
//		timer.scheduleAtFixedRate(frameGrabber, 0, 1/60, TimeUnit.SECONDS)
//	}
//
//	private fun getCapture(capture: VideoCapture): Image {
//		val mat = Mat()
//		capture.read(mat)
//		return mat2Img(mat)
//	}
//
//	private fun getCaptureWithFaceDetection(capture: VideoCapture = VideoCapture()): Image {
//		val mat = Mat()
//		capture.read(mat)
//		return mat2Img(detectFace(mat))
//	}
//
//	override fun start(primaryStage: Stage?) {
//		loadCamera(primaryStage!!)
//	}
//
//	fun main(){
//		launch()
//	}
//}