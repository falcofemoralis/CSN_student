import React, { Component } from 'react'
import Modal from 'react-modal';
import ConsoleWindow from '../ConsoleWindow/ConsoleWindow';
import Users from '../Users/Users';
import './App.css';

export default class App extends Component {
  constructor(props) {
    super(props);

    this.state = { uploadedFile: null, logs: null, showModal: false, request: null };
    this.fileInput = React.createRef();
    this.convertFile = this.convertFile.bind(this);
    this.handleOpenModal = this.handleOpenModal.bind(this);
    this.handleCloseModal = this.handleCloseModal.bind(this);
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

  handleOpenModal(n, url, method, data) {
    this.setState({ [`showModal${n}`]: true });
  }

  handleCloseModal(n) {
    this.setState({ [`showModal${n}`]: false });
  }

  handleBtn(url, method, data = null) {
    this.setState({ request: { url: url, method: method, data: data } });
    this.handleOpenModal(2);
  }

  render() {
    const uploadedFile = this.state.uploadedFile;
    return (
      <div className="app">
        <h3 className="headerText">Админ панель CSN Student</h3>
        <div className="main">
          <div>
            <div className="menus">
              <div>
                <h4 className="sub-headerText">Настройка расписания</h4>
                <div>
                  <div className="controls">
                    <div>
                      <input
                        ref={this.fileInput}
                        onChange={(event) => { this.setState({ uploadedFile: event.target.files[0] }); }}
                        type="file"
                        style={{ display: "none" }}
                        accept=".txt"
                      />
                      <button onClick={() => { this.fileInput.current.click(); }}>Загрузить файл</button>
                    </div>
                    {
                      uploadedFile &&
                      <button className="convert-bt" onClick={this.convertFile}>Конвертировать файл</button>
                    }
                    <button onClick={() => { this.handleBtn("/schedule/new", "PUT") }}>Обновить расп.</button>
                    <button onClick={() => { this.handleBtn("/schedule/reset", "DELETE") }}>Очистить расп.</button>
                    <button onClick={() => { this.handleOpenModal(1) }}>Файл с расписанием?</button>
                    <Modal isOpen={this.state.showModal1} className="modal" overlayClassName="overlay">
                      <h4 className="sub-headerText">Как получить файл .txt с расписанием</h4>
                      <div className="hint">
                        <p>1) Преобразовать пдф файл <a href="https://products.aspose.app/pdf/ru/parser/pdf">PDF converter</a></p>
                        <p>2) Перевести кодировку в UTF-8</p>
                      </div>
                      <button onClick={() => { this.handleCloseModal(1) }}>Закрыть</button>
                    </Modal>
                  </div>
                </div>
              </div>
              <div>
                <h4 className="sub-headerText">Настройка кеша</h4>
                <div>
                  <div className="controls">
                    <button onClick={() => { this.handleBtn("/api/cache/create", "POST") }}>Создать кеш</button>
                    <button onClick={() => { this.handleBtn("/api/cache/recreate", "PUT", "groupsApi") }}>Обновить группы</button>
                    <button onClick={() => { this.handleBtn("/api/cache/recreate", "PUT", "subjectsApi") }}>Обновить предметы</button>
                    <button onClick={() => { this.handleBtn("/api/cache/recreate", "PUT", "teachersApi") }}>Обновить преподов</button>
                  </div>
                </div>
              </div>
            </div>
            <ConsoleWindow logs={this.state.logs} />
          </div>
          <Users />
        </div>
        <Modal isOpen={this.state.showModal2} className="modal" overlayClassName="overlay">
          <h4 className="sub-headerText">Подтвердите свои действия</h4>
          <button onClick={() => { this.HTTPRequest(this.state.request.url, this.state.request.method, this.state.request.data); this.handleCloseModal(2) }}>Подтвердить</button>
          <button onClick={() => { this.handleCloseModal(2) }}>Отмена</button>
        </Modal>
      </div>
    )
  }
}