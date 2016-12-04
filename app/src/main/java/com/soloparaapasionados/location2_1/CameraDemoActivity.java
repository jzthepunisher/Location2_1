/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.soloparaapasionados.location2_1;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveCanceledListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Esto muestra como cambiar la posicion de la camara para el mapa.
 */
public class CameraDemoActivity extends AppCompatActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {

    private static final String TAG = CameraDemoActivity.class.getName();

    /**La cantidad de desplazamiento de la cámara. Tenga en cuenta que esta cantidad está en píxeles en bruto, no dp
     * (píxeles independientes de la densidad).*
     * The amount by which to scroll the camera. Note that this amount is in raw pixels, not dp
     * (density-independent pixels). */
    private static final int SCROLL_BY_PX = 100;

    public static final CameraPosition BONDI =
            new CameraPosition.Builder().target(new LatLng(-33.891614, 151.276417))
                    .zoom(15.5f)
                    .bearing(300)
                    .tilt(90)
                    .build();

    public static final CameraPosition SYDNEY =
            new CameraPosition.Builder().target(new LatLng(-33.87365, 151.20689))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();

    private GoogleMap mMap;

    private CompoundButton mAnimateToggle;
    private CompoundButton mCustomDurationToggle;
    private SeekBar mCustomDurationBar;
    private PolylineOptions currPolylineOptions;
    private boolean isCanceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_demo);

        mAnimateToggle = (CompoundButton) findViewById(R.id.animate);
        mCustomDurationToggle = (CompoundButton) findViewById(R.id.duration_toggle);
        mCustomDurationBar = (SeekBar) findViewById(R.id.duration_bar);

        updateEnabledState();

        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEnabledState();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

        // Nosotros proporcionaremos nuestro propio controles de zoom-We will provide our own zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Muestra Sidney-Show Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.87365, 151.20689), 10));
    }

    /** Cuando el mapa no esta listo el CameraUpdateFactory no puede ser usado. Esto deberia ser llamado sobre
     * todos los puntos de entrada que metodos llama sobre el Google Maps API.
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** LLamado cuando el botón Go To Bondi es clicleado.
     * Called when the Go To Bondi button is clicked.
     */
    public void onGoToBondi(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.newCameraPosition(BONDI));
    }

    /** LLamado cuando el boton Animate to Sidney es clicleado.
     * Called when the Animate To Sydney button is clicked.*/
    public void onGoToSydney(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), new CancelableCallback() {
            @Override
            public void onFinish() {
                Toast.makeText(getBaseContext(), "Animation to Sydney complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(), "Animation to Sydney canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**Llamado cuando el boton parar es clicleado
     * Called when the stop button is clicked. */
    public void onStopAnimation(View view) {
        if (!checkReady()) {
            return;
        }

        mMap.stopAnimation();
    }

    /** Llamado cuando el boton zoom in (el uno con el +) es clicleado.
     * Called when the zoom in button (the one with the +) is clicked.*/
    public void onZoomIn(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomIn());
    }

    /**LLamado cuando el boton zoom out( el uno con el -) es clicleado.
     * Called when the zoom out button (the one with the -) is clicked.*/
    public void onZoomOut(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomOut());
    }

    /**Llamado cuando el boton inclinación más ( el uno con el /) es clicleado.
     ** Called when the tilt more button (the one with the /) is clicked. */
    public void onTiltMore(View view) {
        if (!checkReady()) {
            return;
        }

        CameraPosition currentCameraPosition = mMap.getCameraPosition();
        float currentTilt = currentCameraPosition.tilt;
        float newTilt = currentTilt + 10;

        newTilt = (newTilt > 90) ? 90 : newTilt;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition).tilt(newTilt).build();

        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** LLamado cuando el boton inclinacion menos ( el uno con el \) es clicleado.
     * Called when the tilt less button (the one with the \) is clicked.
     */
    public void onTiltLess(View view) {
        if (!checkReady()) {
            return;
        }

        CameraPosition currentCameraPosition = mMap.getCameraPosition();

        float currentTilt = currentCameraPosition.tilt;

        float newTilt = currentTilt - 10;
        newTilt = (newTilt > 0) ? newTilt : 0;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).build();

        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**Se llama cuando se hace clic en el botón de flecha hacia la izquierda. Esto hace que la cámara se mueva hacia la izquierda
     * Called when the left arrow button is clicked. This causes the camera to move to the left */
    public void onScrollLeft(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
    }

    /**Se llama cuando se hace clic en el botón de flecha derecha. Esto hace que la cámara se mueva hacia la derecha.
     * Called when the right arrow button is clicked. This causes the camera to move to the right.*/
    public void onScrollRight(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
    }

    /**Se llama cuando se hace clic en el botón de flecha hacia arriba. Las causas de la cámara para subir.
     * Called when the up arrow button is clicked. The causes the camera to move up. */
    public void onScrollUp(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
    }

    /**Se llama cuando se hace clic en el botón de flecha hacia abajo. Esto hace que la cámara se mueva hacia abajo.
     * Called when the down arrow button is clicked. This causes the camera to move down.*/
    public void onScrollDown(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX));
    }

    /**Se llama cuando se alterna el botón animado
     * Called when the animate button is toggled*/
    public void onToggleAnimate(View view) {
        updateEnabledState();
    }

    /**Se llama cuando se activa la casilla de verificación de duración personalizada
     * Called when the custom duration checkbox is toggled*/
    public void onToggleCustomDuration(View view) {
        updateEnabledState();
    }

    /** Actualizar el estado habilitado de los controles de duración personalizada.
     * Update the enabled state of the custom duration controls.*/
    private void updateEnabledState() {
        mCustomDurationToggle.setEnabled(mAnimateToggle.isChecked());
        mCustomDurationBar.setEnabled(mAnimateToggle.isChecked() && mCustomDurationToggle.isChecked());
    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    /**Cambie la posición de la cámara mediante el movimiento o animacion de la cámara dependiendo del estado del
     * botón de alternar animado.
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.*/
    private void changeCamera(CameraUpdate update, CancelableCallback callback) {
        if (mAnimateToggle.isChecked()) {
            if (mCustomDurationToggle.isChecked()) {
                int duration = mCustomDurationBar.getProgress();
                // The duration must be strictly positive so we make it at least 1.
                mMap.animateCamera(update, Math.max(duration, 1), callback);
            } else {
                mMap.animateCamera(update, callback);
            }
        } else {
            mMap.moveCamera(update);
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (!isCanceled) {
            mMap.clear();
        }

        String reasonText = "UNKNOWN_REASON";
        currPolylineOptions = new PolylineOptions().width(5);
        switch (reason) {
            case OnCameraMoveStartedListener.REASON_GESTURE:
                currPolylineOptions.color(Color.BLUE);
                reasonText = "GESTURE";
                break;
            case OnCameraMoveStartedListener.REASON_API_ANIMATION:
                currPolylineOptions.color(Color.RED);
                reasonText = "API_ANIMATION";
                break;
            case OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                currPolylineOptions.color(Color.GREEN);
                reasonText = "DEVELOPER_ANIMATION";
                break;
        }
        Log.i(TAG, "onCameraMoveStarted(" + reasonText + ")");
        addCameraTargetToPath();
    }

    @Override
    public void onCameraMove() {
        //Cuando la cámara se está moviendo, añada su objetivo a la ruta actual que dibujaremos en el mapa.
        // When the camera is moving, add its target to the current path we'll draw on the map.
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
        }
        Log.i(TAG, "onCameraMove");
    }

    @Override
    public void onCameraMoveCanceled() {
        //Cuando la cámara deje de moverse, añada su objetivo a la ruta actual y dibujala en el mapa.
        // When the camera stops moving, add its target to the current path, and draw it on the map.
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
            mMap.addPolyline(currPolylineOptions);
        }
        isCanceled = true;  // Establezca para borrar el mapa cuando arrastre vuelva a arrancar.-Set to clear the map when dragging starts again.
        currPolylineOptions = null;
        Log.i(TAG, "onCameraMoveCancelled");
    }

    @Override
    public void onCameraIdle() {
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
            mMap.addPolyline(currPolylineOptions);
        }
        currPolylineOptions = null;
        isCanceled = false;  // Establezca a * no * borrar el mapa al arrancar.-Set to *not* clear the map when dragging starts again.
        Log.i(TAG, "onCameraIdle");
    }

    private void addCameraTargetToPath() {
        LatLng target = mMap.getCameraPosition().target;
        currPolylineOptions.add(target);
    }
}