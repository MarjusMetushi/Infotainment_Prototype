#include <iostream>
#include <windows.h>
#include <mmdeviceapi.h>
#include <endpointvolume.h>
#include <comdef.h>  // For _com_error
// Method to get the system volume
void GetSystemVolume() {
    // Initialize COM library for core audio interaction
    if (FAILED(CoInitialize(nullptr))) {
        std::cerr << "Failed to initialize COM." << std::endl;
        return;
    }
    // Create device enumerator
    IMMDeviceEnumerator* deviceEnumerator = nullptr;
    HRESULT hr = CoCreateInstance(
        __uuidof(MMDeviceEnumerator), nullptr, CLSCTX_ALL,
        __uuidof(IMMDeviceEnumerator), reinterpret_cast<void**>(&deviceEnumerator));
    // check if the device enumerator was created successfully
    if (FAILED(hr) || !deviceEnumerator) {
        std::cerr << "Failed to create MMDeviceEnumerator: " << _com_error(hr).ErrorMessage() << std::endl;
        CoUninitialize();
        return;
    }
    // Get the default audio endpoint
    IMMDevice* defaultDevice = nullptr;
    hr = deviceEnumerator->GetDefaultAudioEndpoint(eRender, eMultimedia, &defaultDevice);
    // Check if the default device was found
    if (FAILED(hr) || !defaultDevice) {
        std::cerr << "Failed to get default audio endpoint: " << _com_error(hr).ErrorMessage() << std::endl;
        deviceEnumerator->Release();
        CoUninitialize();
        return;
    }
    // Activate the endpoint volume interface
    IAudioEndpointVolume* endpointVolume = nullptr;
    hr = defaultDevice->Activate(
        __uuidof(IAudioEndpointVolume), CLSCTX_ALL, nullptr, reinterpret_cast<void**>(&endpointVolume));
    // If the activation failed, release the default device and return
    if (FAILED(hr) || !endpointVolume) {
        std::cerr << "Failed to activate audio endpoint volume: " << _com_error(hr).ErrorMessage() << std::endl;
        defaultDevice->Release();
        deviceEnumerator->Release();
        CoUninitialize();
        return;
    }
    // Query the computer's volume level
    float volume = 0.0f;
    hr = endpointVolume->GetMasterVolumeLevelScalar(&volume);
    // Check if the query was successful to convert the volume level to a percentage
    if (SUCCEEDED(hr)) {
        int volumePercentage = static_cast<int>(volume * 100);
        std::cout << "Current System Volume: " << volumePercentage << "%" << std::endl;
    } else {
        std::cerr << "Failed to get master volume level: " << _com_error(hr).ErrorMessage() << std::endl;
    }

    // Cleanup
    endpointVolume->Release();
    defaultDevice->Release();
    deviceEnumerator->Release();
    CoUninitialize();
}

int main() {
    GetSystemVolume();
    return 0;
}
// Run and compile the program to make an executable
// Run the executable from a java program to retrieve the data found here