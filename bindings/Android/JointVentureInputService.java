package com.jointventure.keyboard;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.jv.npu.ClipboardGuard; // Ensure this import matches your package

public class JointVentureInputService extends InputMethodService {

    private ClipboardGuard clipboardGuard;
    private View mCandidateView;

    // --- 1. Lifecycle: Initialization ---
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the guard so resetTimer() doesn't crash the service
        clipboardGuard = new ClipboardGuard(this);
    }

    // --- 2. UI: The Prediction Toolbar (The Face) ---
    @Override
    public View onCreateCandidatesView() {
        // Inflate the surgical toolbar we built in XML
        mCandidateView = getLayoutInflater().inflate(R.layout.prediction_toolbar, null);
        
        // Setup Clipboard Button
        View btnClipboard = mCandidateView.findViewById(R.id.btn_clipboard);
        btnClipboard.setOnClickListener(v -> {
            Log.d("JV_UI", "Clipboard Button Pressed - Manual Flush Triggered");
            clipboardGuard.manualFlush(); // Added this for manual control
        });

        // Setup NPU Mic Button
        View btnMic = mCandidateView.findViewById(R.id.btn_mic);
        btnMic.setOnClickListener(v -> {
            Log.d("JV_UI", "NPU Mic Pressed - Hexagon V79 Listening");
        });

        return mCandidateView;
    }

    // --- 3. Input Logic: Triggers ---
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        setCandidatesViewShown(true); // Always show our NPU toolbar
        clipboardGuard.resetTimer();  // Reset the 20-min "Dead Man's Switch"
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        // "Joint Venture" logic: Reset NPU context for new fields here
    }

    @Override
    public View onCreateInputView() {
        // Eventually we inflate the custom JSON keyboard layout here
        return super.onCreateInputView();
    }

    // --- 4. CONSOLIDATED NPU NATIVE BRIDGE ---
    static {
        // The Silicon Bridge to the C++ Core
        System.loadLibrary("jv_npu_engine");
    }

    /**
     * Initializes the Hexagon NPU with our Spanish/English weights.
     */
    public native void initNPUEngine(String modelPath);

    /**
     * Sends the current text buffer to the NPU and gets semantic predictions.
     */
    public native String[] getNPUPredictions(String input);
}
