import QRCode from 'qrcode';
import { useEffect, useRef, useState } from 'react';
import axios from 'axios';
import useWebSocket from 'react-use-websocket';

const WS_URL = 'ws://127.0.0.1:8080/ws';

function App() {
  const [qr, setQr] = useState(null);
  const [authLog, setAuth] = useState(null);

  useEffect(() => {
    console.log('qr changed:', qr);
  }, [qr]);

  const onMessageReceived = (event) => {
    try {
      const message = JSON.parse(event.data);
      console.log('Websocket message received', message);

      // QR code received
      if (message.event === 'qr') {
        console.log('qr received', message);
        setQr(message.data);
      }

      // Authenticated
      if (message.event === 'authentication') {
        console.log('authentication', message.status);
        setAuth(message.status);
      }
    } catch (error) {
      console.error('Error parsing JSON data', error);
    }
  };

  useWebSocket(WS_URL, {
    onOpen: () => console.log('Websocket connection opened'),
    onMessage: onMessageReceived,
  });

  return (
    <div className="App">
      {authLog ? (
        <>
          <h1> Authentication status:</h1>
          <h2><pre>{authLog}</pre></h2>
        </>
      ) : (
        <>
          <h1>Welcome.</h1>
          <h2>Before you continue, please verify your age.</h2>
          <div className="phone">CONTINUE ON YOUR MOBILE DEVICE</div>
          {qr ? (
            <img src={`data:image/png;base64,${qr}`} alt="QR Code" className="qr" />
          ) : (
            <p>Generating QR Code...</p>
          )}
        </>
      )}
    </div>
  );
}

export default App;
