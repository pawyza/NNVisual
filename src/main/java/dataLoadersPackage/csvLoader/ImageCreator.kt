package dataLoadersPackage.csvLoader

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_BYTE_GRAY

object ImageCreator{

     fun displayData(imageArray : Array<Double>, scale : Int) : BufferedImage{
        require(scale >= 1){"Scale must be positive number"}
        val image = BufferedImage(28*scale,28*scale,TYPE_BYTE_GRAY)
        val graphic = image.graphics
        imageArray.mapIndexed { index, value ->
            graphic.color = Color((value*255).toInt(),(value*255).toInt(),(value*255).toInt())
            graphic.fillRect((index%28)*scale,(index/28)*scale,scale,scale)
        }
        return image
    }
}