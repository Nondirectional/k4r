package com.non.k4r.tooling;

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "small-phone", device = Devices.PIXEL_4A)
@Preview(name = "phone", device = Devices.PIXEL_7_PRO)
@Preview(name = "landscape", device = "spec:width=640dp,height=360dp,dpi=480")
@Preview(name = "foldable", device = "spec:width=673dp,height=841dp")
@Preview(name = "tablet", device = Devices.PIXEL_TABLET)
annotation class DevicePreviews