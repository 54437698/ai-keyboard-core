@Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        
        // 1. ADD VIBRATION (Haptic Feedback)
        if (kv != null) {
            kv.performHapticFeedback(
                android.view.HapticFeedbackConstants.KEYBOARD_TAP,
                android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            );
        }

        if (ic == null) return;

        switch (primaryCode) {
            case -100: // THE G-BUTTON: Launch Settings
                android.content.Intent intent = new android.content.Intent(this, SettingsActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
                
            case -5: // Delete
                ic.deleteSurroundingText(1, 0);
                break;
                
            case -1: // Shift
                handleShiftLogic(); // Use your existing shift function!
                break;
            
            case -2: // Symbols (?123)
                toggleKeyboardLayout(); // Use your existing toggle function!
                break;
                
            case -4: // Enter
                ic.sendKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER));
                break;

            case 32: // Space
                ic.commitText(" ", 1);
                break;

            default:
                char code = (char) primaryCode;
                // Use your existing 'isCaps' variable here
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
                
                // If it was a one-time shift (not capslock), turn it off after typing
                if (isCaps && !mCapsLock) {
                    isCaps = false;
                    mKeyboard.setShifted(false);
                    kv.invalidateAllKeys();
                }
        }
    }
