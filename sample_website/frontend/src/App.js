import QRCode from 'qrcode'
import {useEffect, useState} from 'react'

function App() {
  var canvas = document.getElementById('canvas')

  useEffect(() => {
    fetch('http://127.0.0.1:8080/qr')
    .then(response => response.text())
    .then(data => {
      QRCode.toCanvas(canvas, data, function (error) {
        if (error) console.error(error)
        console.log('success!');
      })
    })
  }, [])




  return (
    <div className="App">
      <h1>Welcome.</h1>
      <h2>Before you continue, please verify your age.</h2>
      <div className="phone">
        CONTINUE ON YOUR MOBILE DEVICE
      </div>
      <canvas id="canvas"></canvas>
    </div>
  )
}

export default App