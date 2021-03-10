import React, { Component } from 'react'
import ConsoleWindow from '../ConsoleWindow/ConsoleWindow';
import './App.css';

export default class App extends Component {
  constructor(props) {
    super(props);

    this.state = { uploadedFile: null, logs: null };
    this.fileInput = React.createRef();
    this.convertFile = this.convertFile.bind(this);
  }

  /**
   * Отапрвка файла на преобразование в JSON
   */
  convertFile() {
    let formData = new FormData();
    formData.append('file', this.state.uploadedFile);
    this.HTTPRequest("/schedule/upload", 'POST', formData);
  }

  /**
   * Запрос к серверу 
   * @param {*} url - путь
   * @param {*} method - метод
   * @param {*} data - данные
   */
  HTTPRequest(url, method, data = null) {
    fetch('http://192.168.0.104:81' + url, {
      method: method,
      body: data
    }).then(response => {
      if (response.ok) {
        response.text().then((text) => {
          console.log(text);
          this.setState({ logs: text });
        });
      }
    });
  }

  render() {
    const uploadedFile = this.state.uploadedFile;

    return (
      <div className="App">
        <span>    //1) преобразовать пдф файл
        //https://products.aspose.app/pdf/ru/parser/pdf
     //2) перевести кодировку в UTF-8</span>
        <div>
          <input
            ref={this.fileInput}
            onChange={(event) => { this.setState({ uploadedFile: event.target.files[0] }); }}
            type="file"
            style={{ display: "none" }}
            accept=".txt"
          />
          <button onClick={() => { this.fileInput.current.click(); }}>Upload File</button>
        </div>
        {
          uploadedFile &&
          <button onClick={this.convertFile}>Конвертировать файл</button>
        }
        <button onClick={() => { this.HTTPRequest("/schedule/new", 'PUT'); }}>Обновить базу</button>
        <button onClick={() => { this.HTTPRequest("/schedule/reset", 'DELETE'); }}>Очистить базу</button>
        <ConsoleWindow logs={this.state.logs} />
      </div>
    )
  }
}