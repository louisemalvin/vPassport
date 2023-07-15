import QRCode from 'qrcode'
import {useState} from 'react'

function App() {

  const [qr, setQr] = useState('')
  const generateQR =  () => {
    QRCode.toDataURL('https://www.google.com', (err, url) => {
      if (err) return console.log("error occurred")
      console.log(url)
      setQr(url)
    })  
  }
  return (
    <div className="App">
      <h1>Welcome.</h1>
      <h2>Before you continue, please verify your age.</h2>
      <div className="phone" onClick={generateQR()}>
        CONTINUE ON YOUR MOBILE DEVICE
      </div>
      <button onClick={generateQR}>Generate QR</button>
      <img src={qr} alt="qr" />
    </div>
  )
}

export default App