/*
 * Copyright (C) 2022 Bryan Parker
 *
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.experiments

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var layout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a new coroutine since repeatOnLifecycle is a suspend function
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
                    .collect { newLayoutInfo ->
                        updateCurrentLayout(newLayoutInfo)
                    }
            }
        }

        motionLayout()
    }

    private fun motionLayout() {
        setContentView(R.layout.activity_motion_layout)
        layout = findViewById(R.id.root)
        findViewById<View>(R.id.switchToConstraint).setOnClickListener {
            constraintLayout()
        }
    }

    private fun constraintLayout() {
        setContentView(R.layout.activity_constraint_layout)
        layout = findViewById(R.id.root)
        findViewById<View>(R.id.switchToMotion).setOnClickListener {
            motionLayout()
        }
    }

    /**
     * Returns the position of the fold relative to the view
     */
    private fun foldPosition(view: View, foldingFeature: FoldingFeature): Int {
        val splitRect = getFeatureBoundsInWindow(foldingFeature, view)
        splitRect?.let {
            return splitRect.top
        }

        return 0
    }

    /**
     * Get the bounds of the display feature translated to the View's coordinate space and current
     * position in the window. This will also include view padding in the calculations.
     */
    private fun getFeatureBoundsInWindow(
        displayFeature: DisplayFeature,
        view: View,
        includePadding: Boolean = true
    ): Rect? {
        // The the location of the view in window to be in the same coordinate space as the feature.
        val viewLocationInWindow = IntArray(2)
        view.getLocationInWindow(viewLocationInWindow)

        // Intersect the feature rectangle in window with view rectangle to clip the bounds.
        val viewRect = Rect(
            viewLocationInWindow[0], viewLocationInWindow[1],
            viewLocationInWindow[0] + view.width, viewLocationInWindow[1] + view.height
        )

        // Include padding if needed
        if (includePadding) {
            viewRect.left += view.paddingLeft
            viewRect.top += view.paddingTop
            viewRect.right -= view.paddingRight
            viewRect.bottom -= view.paddingBottom
        }

        val featureRectInView = Rect(displayFeature.bounds)
        val intersects = featureRectInView.intersect(viewRect)

        // Checks to see if the display feature overlaps with our view at all
        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
            !intersects
        ) {
            return null
        }

        // Offset the feature coordinates to view coordinate space start point
        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])

        return featureRectInView
    }

    /**
     * Update the current layout based on the passed WindowLayoutInfo
     */
    private fun updateCurrentLayout(newLayoutInfo: WindowLayoutInfo) {
        // Add views that represent display features
        for (displayFeature in newLayoutInfo.displayFeatures) {
            val foldFeature = displayFeature as? FoldingFeature
            if (foldFeature != null) {
                if (isTableTopMode(foldFeature)) {
                    // The foldable device is in tabletop mode
                    val fold = foldPosition(layout, foldFeature)
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold_constraint_layout, fold)
                    // The fold position is offset from the start of the layout so subtract the height to get it offset the end for MotionLayout
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold_motion_layout, layout.height - fold)
                } else {
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold_motion_layout, 0)
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold_constraint_layout, layout.height)
                }
            }
        }
    }

    /**
     * Returns true if the current posture is TableTop
     */
    private fun isTableTopMode(foldFeature: FoldingFeature) =
        foldFeature.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
}
