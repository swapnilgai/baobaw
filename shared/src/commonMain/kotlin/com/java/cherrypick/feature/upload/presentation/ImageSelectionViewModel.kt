package com.java.cherrypick.feature.upload.presentation

import com.java.cherrypick.feature.upload.interactor.ImageUploadInteractor
import com.java.cherrypick.interactor.interactorLaunch
import com.java.cherrypick.presentationInfra.BaseViewModel
import dev.icerock.moko.media.Bitmap
import dev.icerock.moko.media.picker.MediaPickerController
import dev.icerock.moko.media.picker.MediaSource
import kotlinx.coroutines.launch

data class ImageSelectionContent(val data: Bitmap? = null)

class ImageSelectionViewModel(private val imageUploadInteractor: ImageUploadInteractor) : BaseViewModel<ImageSelectionContent>(initialContent = ImageSelectionContent()) {

    fun onGalleryPressed(mediaPickerController: MediaPickerController) {
        selectImage(MediaSource.GALLERY, mediaPickerController)
    }

    fun onCameraPressed(mediaPickerController: MediaPickerController) {
        selectImage(MediaSource.CAMERA, mediaPickerController)
    }

    private fun selectImage(source: MediaSource, mediaPickerController: MediaPickerController) {
        viewModelScope.interactorLaunch {
                @Suppress("SwallowedException")
                val image = mediaPickerController.pickImage(source)
                setContent { copy(data = image) }
        }
    }

    fun uploadImage(bitmap: Bitmap, index: Int = 1){
        viewModelScope.interactorLaunch {
            setLoading()
            imageUploadInteractor.imageUpload(bitmap, index)
            setContent {
                copy()
            }
        }
    }

    fun deleteImage(index: Int = 1){
        viewModelScope.interactorLaunch {
            setLoading()
            imageUploadInteractor.deleteImage(index)
            setContent {
                copy()
            }
        }
    }


//    fun getUrlFromId(id: String){
//        viewModelScope.launch {
//            setLoading()
//            imageUploadInteractor.getUrlFromId(id)
//            setContent {
//                copy()
//            }
//        }
//    }

}