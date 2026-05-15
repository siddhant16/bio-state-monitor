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
    expect(screen.getByText(/Bio-State Monitor/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /analyze/i })).toBeInTheDocument();
    expect(screen.getByRole('combobox')).toBeInTheDocument(); // Culture type select
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

    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: 'kombucha' } });

    expect(select.value).toBe('kombucha');
  });
});