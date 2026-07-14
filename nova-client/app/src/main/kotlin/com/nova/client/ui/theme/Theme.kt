package com.nova.client.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.nova.client.data.models.NovaTheme

@Composable
fun NovaClientTheme(
    novaTheme: NovaTheme = NovaTheme.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (novaTheme) {
        NovaTheme.DARK -> darkColorScheme(
            background        = DarkColors.Background,
            surface           = DarkColors.Surface,
            surfaceVariant    = DarkColors.SurfaceVariant,
            primary           = DarkColors.Primary,
            primaryContainer  = DarkColors.PrimaryVariant,
            secondary         = DarkColors.Accent,
            onBackground      = DarkColors.OnBackground,
            onSurface         = DarkColors.OnSurface,
            onPrimary         = DarkColors.OnPrimary,
            error             = DarkColors.Error,
            outline           = DarkColors.Border
        )
        NovaTheme.NEON -> darkColorScheme(
            background        = NeonColors.Background,
            surface           = NeonColors.Surface,
            surfaceVariant    = NeonColors.SurfaceVariant,
            primary           = NeonColors.Primary,
            primaryContainer  = NeonColors.PrimaryVariant,
            secondary         = NeonColors.Accent,
            onBackground      = NeonColors.OnBackground,
            onSurface         = NeonColors.OnSurface,
            onPrimary         = NeonColors.OnPrimary,
            error             = NeonColors.Error,
            outline           = NeonColors.Border
        )
        NovaTheme.AMOLED -> darkColorScheme(
            background        = AmoledColors.Background,
            surface           = AmoledColors.Surface,
            surfaceVariant    = AmoledColors.SurfaceVariant,
            primary           = AmoledColors.Primary,
            primaryContainer  = AmoledColors.PrimaryVariant,
            secondary         = AmoledColors.Accent,
            onBackground      = AmoledColors.OnBackground,
            onSurface         = AmoledColors.OnSurface,
            onPrimary         = AmoledColors.OnPrimary,
            error             = AmoledColors.Error,
            outline           = AmoledColors.Border
        )
        NovaTheme.MINIMAL -> lightColorScheme(
            background        = MinimalColors.Background,
            surface           = MinimalColors.Surface,
            surfaceVariant    = MinimalColors.SurfaceVariant,
            primary           = MinimalColors.Primary,
            primaryContainer  = MinimalColors.PrimaryVariant,
            secondary         = MinimalColors.Accent,
            onBackground      = MinimalColors.OnBackground,
            onSurface         = MinimalColors.OnSurface,
            onPrimary         = MinimalColors.OnPrimary,
            error             = MinimalColors.Error,
            outline           = MinimalColors.Border
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = NovaTypography,
        content     = content
    )
}
