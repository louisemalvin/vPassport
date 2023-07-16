import QRCode from 'qrcode'
import { useEffect, useRef, useState } from 'react'
import axios from 'axios'

function App() {

  return (
    <div className="App">
      <h1>Welcome.</h1>
      <h2>Before you continue, please verify your age.</h2>
      <div className="phone">
        CONTINUE ON YOUR MOBILE DEVICE
      </div>
      <img className= "qr" src="http://127.0.0.1:8080/api/qr"></img>
    </div>
  )
}

export default App