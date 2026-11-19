package com.fasterxml.jackson.databind;

/* JADX INFO: loaded from: classes3.dex */
public class EnumNamingStrategies {

    public static class CamelCaseStrategy implements EnumNamingStrategy {
        public static final CamelCaseStrategy INSTANCE = new CamelCaseStrategy();

        private static char charToLowerCaseIfUpper(char c5) {
            return Character.isUpperCase(c5) ? Character.toLowerCase(c5) : c5;
        }

        private static char charToUpperCaseIfLower(char c5) {
            return Character.isLowerCase(c5) ? Character.toUpperCase(c5) : c5;
        }

        private static int indexIn(CharSequence charSequence, int i10) {
            int length = charSequence.length();
            while (i10 < length) {
                if ('_' == charSequence.charAt(i10)) {
                    return i10;
                }
                i10++;
            }
            return -1;
        }

        private static String normalizeWord(String str) {
            int length = str.length();
            if (length == 0) {
                return str;
            }
            StringBuilder sb2 = new StringBuilder(length);
            sb2.append(charToUpperCaseIfLower(str.charAt(0)));
            sb2.append(toLowerCase(str.substring(1)));
            return sb2.toString();
        }

        private static String toLowerCase(String str) {
            int length = str.length();
            StringBuilder sb2 = new StringBuilder(length);
            for (int i10 = 0; i10 < length; i10++) {
                sb2.append(charToLowerCaseIfUpper(str.charAt(i10)));
            }
            return sb2.toString();
        }

        @Override // com.fasterxml.jackson.databind.EnumNamingStrategy
        public String convertEnumToExternalName(String str) {
            StringBuilder sb2 = null;
            if (str == null) {
                return null;
            }
            int i10 = 0;
            int iIndexIn = -1;
            do {
                iIndexIn = indexIn(str, iIndexIn + 1);
                if (iIndexIn != -1) {
                    if (i10 == 0) {
                        sb2 = new StringBuilder(str.length() + 4);
                        sb2.append(toLowerCase(str.substring(i10, iIndexIn)));
                    } else {
                        sb2.append(normalizeWord(str.substring(i10, iIndexIn)));
                    }
                    i10 = iIndexIn + 1;
                }
            } while (iIndexIn != -1);
            if (i10 == 0) {
                return toLowerCase(str);
            }
            sb2.append(normalizeWord(str.substring(i10)));
            return sb2.toString();
        }
    }

    private EnumNamingStrategies() {
    }
}
