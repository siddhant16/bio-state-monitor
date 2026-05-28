import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

// Mock navigator.mediaDevices.getUserMedia
Object.defineProperty(navigator, 'mediaDevices', {
  value: {
    getUserMedia: jest.fn().mockResolvedValue({
      getTracks: jest.fn().mockReturnValue([{ stop: jest.fn() }])
    })
  },
  writable: true
});

// Mock video element methods
HTMLVideoElement.prototype.play = jest.fn().mockResolvedValue();
HTMLVideoElement.prototype.pause = jest.fn();

describe('App Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders the app with camera and controls', () => {
    render(<App />);

    // Check for main elements
    expect(screen.getByText(/Bio-State Fermentation Monitor/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Capture & Run Inference/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Sourdough/i })).toBeInTheDocument(); // Culture type button
  });

  test('displays error when camera access fails', async () => {
    // Mock getUserMedia to reject
    navigator.mediaDevices.getUserMedia.mockRejectedValueOnce(new Error('Permission denied'));

    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Failed to access camera/i)).toBeInTheDocument();
    });
  });

  test('changes culture type', () => {
    render(<App />);

    const sourdoughBtn = screen.getByRole('button', { name: /Sourdough/i });
    const kombuchaBtn = screen.getByRole('button', { name: /Kombucha/i });

    // Initially Sourdough should be active (has bg-blue-600 class)
    expect(sourdoughBtn).toHaveClass('bg-blue-600');

    // Click Kombucha button
    fireEvent.click(kombuchaBtn);

    // Kombucha should be active now
    expect(kombuchaBtn).toHaveClass('bg-blue-600');
  });
});