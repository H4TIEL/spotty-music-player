

package io.github.h4tiel.able.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The splash screen.
 */
@ExperimentalCoroutinesApi
class Splash: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this@Splash, MainActivity::class.java))
        finish()
    }
}