with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\ui\screens\iptv\IptvProviderHomeScreen.kt', encoding='utf-8') as f:
    content = f.read()

# 1. Ajouter les imports manquants
old_imports = 'import androidx.compose.material.icons.filled.LiveTv\nimport androidx.compose.material.icons.filled.Movie\nimport androidx.compose.material.icons.filled.Tv'
new_imports = 'import androidx.compose.material.icons.filled.FiberManualRecord\nimport androidx.compose.material.icons.filled.LiveTv\nimport androidx.compose.material.icons.filled.Movie\nimport androidx.compose.material.icons.filled.Schedule\nimport androidx.compose.material.icons.filled.Tv'
content = content.replace(old_imports, new_imports)

# 2. Ajouter les deux nouveaux params dans la signature
old_sig = '    onNavigateToLiveTv: () -> Unit,\n    onNavigateToMovies: () -> Unit,\n    onNavigateToSeries: () -> Unit\n)'
new_sig = '    onNavigateToLiveTv: () -> Unit,\n    onNavigateToMovies: () -> Unit,\n    onNavigateToSeries: () -> Unit,\n    onNavigateToSchedule: () -> Unit,\n    onNavigateToRecordings: () -> Unit\n)'
content = content.replace(old_sig, new_sig)

# 3. Ajouter la 2e Row apres la fermeture de la 1ere Row
old_row_end = '            }\n        }\n    }\n}'
new_row_end = '''            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContentTypeCard(
                    icon = Icons.Default.Schedule,
                    label = "Programmation",
                    subtitle = "Guide des programmes",
                    accentColor = Color(0xFF00BCD4),
                    onClick = onNavigateToSchedule,
                    modifier = Modifier.weight(1f)
                )
                ContentTypeCard(
                    icon = Icons.Default.FiberManualRecord,
                    label = "Enregistrements",
                    subtitle = "Planifier & gérer",
                    accentColor = Color(0xFFF44336),
                    onClick = onNavigateToRecordings,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}'''
content = content.replace(old_row_end, new_row_end)

with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\ui\screens\iptv\IptvProviderHomeScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
print('Done')
