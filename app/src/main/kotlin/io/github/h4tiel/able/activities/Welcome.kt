

package io.github.h4tiel.able.activities

import android.Manifest
import android.os.Bundle
import io.github.dreierf.materialintroscreen.MaterialIntroActivity
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder
import io.github.h4tiel.able.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The welcome screen that shows up when the required permissions haven't been granted.
 */
@ExperimentalCoroutinesApi
class Welcome: MaterialIntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(
            SlideFragmentBuilder()
                .backgroundColor(R.color.welcome_bg)
                .buttonsColor(R.color.colorAccent)
                .neededPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                .image(io.github.dreierf.materialintroscreen.R.drawable.ic_next)
                .title(getString(R.string.welcome).format("AbleMusic"))
                .description(getString(R.string.storage_perm))
                .build()
        )

        addSlide(
            SlideFragmentBuilder()
                .backgroundColor(R.color.welcome_bg)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.welcome_icon  )
                .title("Thanks for installing!")
                .description("MusicPlayer")
                .build()
        )
    }
}