with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\ui\screens\iptv\IptvHomeScreen.kt', encoding='utf-8') as f:
    content = f.read()

old = '''    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(14.dp)
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(150), label = "cardBg"
    )
    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = bgColor, focusedContainerColor = bgColor),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProviderStatusIcon(provider, syncState, Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    provider.name,
                    color = NuvioColors.TextPrimary,
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    ProviderTypeBadge(provider.type)
                    Text(
                        providerStatusLabel(provider.status, syncState),
                        color = providerStatusColor(provider.status, syncState),
                        fontSize = 11.sp
                    )
                    provider.expirationDate?.let { exp ->
                        val formatted = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(exp))
                        val color = if (exp < System.currentTimeMillis()) androidx.compose.ui.graphics.Color(0xFFEF5350) else NuvioColors.TextTertiary
                        Text("\\u2022 Exp. $formatted", color = color, fontSize = 11.sp)
                    }
                }
                if (syncState is SyncState.Syncing && syncState.phase.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(syncState.phase, color = NuvioColors.TextTertiary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.width(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ActionIconButton(Icons.Default.Sync, "Sync", enabled = syncState !is SyncState.Syncing, onClick = onSync)
                ActionIconButton(Icons.Default.Edit, "Modifier", onClick = onEditSettings)
                ActionIconButton(Icons.Default.Delete, "Supprimer", tint = NuvioColors.Error, onClick = onDelete)
            }
        }
    }
}'''

new = '''    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(NuvioColors.BackgroundCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Zone cliquable principale (ouvre le provider)
            Card(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
                colors = CardDefaults.colors(containerColor = Color.Transparent, focusedContainerColor = NuvioColors.FocusBackground),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProviderStatusIcon(provider, syncState, Modifier.size(22.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            provider.name,
                            color = NuvioColors.TextPrimary,
                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            ProviderTypeBadge(provider.type)
                            Text(
                                providerStatusLabel(provider.status, syncState),
                                color = providerStatusColor(provider.status, syncState),
                                fontSize = 11.sp
                            )
                            provider.expirationDate?.let { exp ->
                                val formatted = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(exp))
                                val color = if (exp < System.currentTimeMillis()) androidx.compose.ui.graphics.Color(0xFFEF5350) else NuvioColors.TextTertiary
                                Text("\u2022 Exp. $formatted", color = color, fontSize = 11.sp)
                            }
                        }
                        if (syncState is SyncState.Syncing && syncState.phase.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(syncState.phase, color = NuvioColors.TextTertiary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ActionIconButton(Icons.Default.Sync, "Sync", enabled = syncState !is SyncState.Syncing, onClick = onSync)
                ActionIconButton(Icons.Default.Edit, "Modifier", onClick = onEditSettings)
                ActionIconButton(Icons.Default.Delete, "Supprimer", tint = NuvioColors.Error, onClick = onDelete)
            }
        }
    }
}'''

if old in content:
    content = content.replace(old, new)
    with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\ui\screens\iptv\IptvHomeScreen.kt', 'w', encoding='utf-8') as f:
        f.write(content)
    print('Done')
else:
    print('Pattern not found')
