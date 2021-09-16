import React, { Component } from 'react'
import Modal from 'react-modal';
import './Users.css';
import { ServiceData } from '../Services/ServiceData';

export default class Users extends Component {

    constructor(props) {
        super(props);
        this.state = {
            users: [], isModalOpened: false, userlogs: [], logTypesTexts: ["Открыл приложение", "Открыл расписание", "Сменил расписание", "Сменил неделю", "Открыл предметы", "Открыл предмет", "Открыл работу", "Создал работу",
                "Сменил состояние работы", "Удалил работу", "Изменил ценность предмета", "Открыл полную статистику", "Открыл расписание звонков", "Открыл поиск адутории",
                "Сделал поиск комнаты", "Сменил вкладку корпуса", "Сменил вкладку этажа", "Открыл калькулятор рейтинга", "Расчитал рейтинг", "Открыл подсказку калькулятора",
                "Открыл найстроки", "Сменил никнейм", "Сменил пароль", "Сменил язык", "Открыл профиль гитхаб", "Открыл профиль телеграм"],
            subjects: []
        };
    }

    componentDidMount() {
        fetch(ServiceData.DOMAIN + '/api/users/all', {
            method: 'GET'
        }).then(response => {
            if (response.ok) {
                response.json().then((users) => {
                    this.setState({ users: users });
                });
            }
        });

        fetch(ServiceData.DOMAIN + `/api/subjects/shortAll`, {
            method: 'GET'
        }).then(response => {
            if (response.ok) {
                response.json().then((subjects) => {
                    this.setState({ subjects: subjects });
                });
            }
        });
    }

    getUserLogs(userId) {
        fetch(ServiceData.DOMAIN + `/api/users/logs/${userId}`, {
            method: 'GET'
        }).then(response => {
            if (response.ok) {
                response.json().then((userlogs) => {
                    this.setState({ userlogs: userlogs });
                });
            }
        });
    }

    convertLogInfo(logType, logInfo) {
        switch (logType) {
            case 1: break;
        }

        if (logType === "1" || logType === "3") {
            if (logInfo === "0") {
                return "Расписание групп";
            } else {
                return "Расписание учителей";
            }
        }
        else if (logType === "5") {
            return this.getSubjectsName(logInfo);
        } else {
            return logInfo;
        }
    }

    getSubjectsName(id) {
        console.log(this.state.subjects);
        if (this.state.subjects.length > 0) {
            const subjects = this.state.subjects;
            for (let i = 0; i < subjects.length; ++i) {
                if (subjects[i].id == id) {
                    console.log(subjects[i]);
                    return JSON.parse(subjects[i].SubjectName).ru;
                }
            }
        }
    }


    convertTime(unix) {
        const date = new Date(parseInt(unix));
        return date.toLocaleString("ru-RU", { timesoze: "Ukraine/Kiev" });
    }

    render() {
        return (
            <div className="users">
                <ul className="users__list">
                    <li className="users__list-item">
                        <p className="users__list-item-name">Никнейм</p>
                        <p className="users__list-item-opens">Кол-во входов</p>
                        <p className="users__list-item-opens">Последний вход</p>
                        <p className="users__list-item-opens">Logs</p>
                    </li>
                    {this.state.users.map((user) =>
                        <li className="users__list-item" key={user.NickName}>
                            <p className="users__list-item-name table-text">{user.NickName}</p>
                            <p className="users__list-item-opens table-text">{user.Visits}</p>
                            <p className="users__list-item-opens table-text">{user.LastOpen ?? "Не входил"}</p>
                            <button className="opens-logs" onClick={() => { this.setState({ isModalOpened: true }); this.getUserLogs(user.Code_User) }}>Открыть логи</button>
                        </li>
                    )}
                </ul>
                <Modal isOpen={this.state.isModalOpened} className="modal modal__user-logs" overlayClassName="overlay">
                    <h4 className="sub-headerText">Логи пользователя</h4>
                    <ul className="users__list logs__list">
                        <li className="users__list-item">
                            <p className="users__list-item-name log__item">Тип действия</p>
                            <p className="users__list-item-opens log__item">Доп. информация</p>
                            <p className="users__list-item-opens log__item">Время</p>
                        </li>
                        {this.state.userlogs.map((userlog, index) =>
                            <li className="users__list-item" key={index}>
                                <p className="users__list-item-name table-text log__item">{this.state.logTypesTexts[userlog.LogType]}</p>
                                <p className="users__list-item-opens table-text log__item">{this.convertLogInfo(userlog.LogType, userlog.Info)}</p>
                                <p className="users__list-item-opens table-text log__item">{this.convertTime(userlog.PerformedOn)
                                }</p>
                            </li>
                        )}
                    </ul>

                    <button onClick={() => { this.setState({ isModalOpened: false }) }}>Закрыть</button>
                </Modal>
            </div>
        )
    }
}
