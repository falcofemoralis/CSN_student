import React, { Component } from 'react'
import ConsoleWindow from '../ConsoleWindow/ConsoleWindow';
import './App.css';

export default class App extends Component {
  constructor(props) {
    super(props);

    this.state = { uploadedFile: null };
    this.showFileUpload = this.showFileUpload.bind(this);
    this.handleFileUpload = this.handleFileUpload.bind(this);
    this.fileInput = React.createRef();
    this.convertFile = this.convertFile.bind(this);
    this.updateSchedule = this.updateSchedule.bind(this);
    this.resetSchedule = this.resetSchedule.bind(this);
  }

  showFileUpload() {
    this.fileInput.current.click();
  }

  handleFileUpload(event) {
    this.setState({ uploadedFile: event.target.files[0] });
  }

  convertFile() {
    let formData = new FormData();
    formData.append('file', this.state.uploadedFile);

    fetch('http://192.168.0.104:81/schedule/upload', {
      method: 'POST',
      body: formData
    }).then(response => {
      if (response.ok) {
        console.log("Converted");
      }
    });
  }

  updateSchedule() {
    fetch('http://192.168.0.104:81/schedule/new', {
      method: 'PUT'
    }).then(response => {
      if (response.ok) {
        console.log("Updated");
      }
    });
  }

  resetSchedule() {
    fetch('http://192.168.0.104:81/schedule/reset', {
      method: 'DELETE'
    }).then(response => {
      if (response.ok) {
        console.log("Reseted");
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
            onChange={this.handleFileUpload}
            type="file"
            style={{ display: "none" }}
            accept=".txt"
          />
          <button onClick={this.showFileUpload}>Upload File</button>
        </div>
        {
          uploadedFile &&
          <button onClick={this.convertFile}>Конвертировать файл</button>
        }
        <button onClick={this.updateSchedule}>Обновить базу</button>
        <button onClick={this.resetSchedule}>Очистить базу</button>
        <ConsoleWindow name="test" />
      </div>
    )
  }
}