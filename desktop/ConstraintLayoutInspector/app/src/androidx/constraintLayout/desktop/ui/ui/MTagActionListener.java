/*
 * Copyright (C) 2019 The Android Open Source Project
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
 */
package androidx.constraintLayout.desktop.ui.ui;

import androidx.constraintLayout.desktop.ui.adapters.MTag;

/**
 * A small interface to handle keyboard commands that can come from anywhere
 */
public interface MTagActionListener {
  int CONTROL_FLAG = 1;
  void select(MTag selected, int flags);
  void delete(MTag[] tags, int flags);
}
