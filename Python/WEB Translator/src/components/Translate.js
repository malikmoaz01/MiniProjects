import React, { useEffect, useState } from "react";
import countries from "../data";

const Translate = () => {
  const [listening, setListening] = useState(false);
  const [fromText, setFromText] = useState("");

  useEffect(() => {
    const recognition = new window.webkitSpeechRecognition(); // For WebKit-based browsers
    recognition.continuous = false;

    recognition.onstart = () => {
      setListening(true);
    };

    recognition.onend = () => {
      setListening(false);
    };

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      setFromText(transcript);
    };

    const startListening = () => {
      recognition.start();
    };

    const stopListening = () => {
      recognition.stop();
    };

    const fromTextElement = document.querySelector(".from-text");
    const toText = document.querySelector(".to-text");
    const exchangeIcon = document.querySelector(".exchange");
    const selectTags = document.querySelectorAll("select");
    const translateBtn = document.querySelector("button");

    selectTags.forEach((tag, id) => {
      for (let country_code in countries) {
        let selected =
          id === 0
            ? country_code === "en-GB"
              ? "selected"
              : ""
            : country_code === "hi-IN"
            ? "selected"
            : "";
        let option = `<option ${selected} value="${country_code}">${countries[country_code]}</option>`;
        tag.insertAdjacentHTML("beforeend", option);
      }
    });

    exchangeIcon.addEventListener("click", () => {
      let tempText = fromTextElement.value;
      let tempLang = selectTags[0].value;
      fromTextElement.value = toText.value;
      toText.value = tempText;
      selectTags[0].value = selectTags[1].value;
      selectTags[1].value = tempLang;
    });

    fromTextElement.addEventListener("keyup", () => {
      if (!fromTextElement.value) {
        toText.value = "";
      }
    });

    translateBtn.addEventListener("click", () => {
      let text = fromTextElement.value.trim();
      let translateFrom = selectTags[0].value;
      let translateTo = selectTags[1].value;
      if (!text) return;
      toText.setAttribute("placeholder", "Translating...");
      let apiUrl = `https://api.mymemory.translated.net/get?q=${text}&langpair=${translateFrom}|${translateTo}`;
      fetch(apiUrl)
        .then((res) => res.json())
        .then((data) => {
          toText.value = data.responseData.translatedText;
          data.matches.forEach((data) => {
            if (data.id === 0) {
              toText.value = data.translation;
            }
          });
          toText.setAttribute("placeholder", "Translation");
        });
    });

    const icons = document.querySelectorAll(".row i");

    icons.forEach((icon) => {
      icon.addEventListener("click", ({ target }) => {
        if (!fromTextElement.value || !toText.value) return;
        if (target.classList.contains("fa-copy")) {
          if (target.id === "from") {
            navigator.clipboard.writeText(fromTextElement.value);
          } else {
            navigator.clipboard.writeText(toText.value);
          }
        } else if (target.classList.contains("fa-volume-up")) {
          // Handle microphone icon click
          listening ? stopListening() : startListening();
        }
      });
    });

    return () => {
      recognition.stop();
    };
  }, [listening, fromText]);

  return (
    <>
      <div className="container">
        <div className="wrapper">
          <div className="text-input">
            <textarea
              spellCheck="false"
              className="from-text"
              placeholder="Enter text"
              value={fromText}
              onChange={(e) => setFromText(e.target.value)}
            ></textarea>
            <textarea
              spellCheck="false"
              readOnly
              disabled
              className="to-text"
              placeholder="Translation"
            ></textarea>
          </div>
          <ul className="controls">
            <li className="row from">
              <div className="icons">
                <i
                  id="from"
                  className={`fas fa-volume-up${listening ? " active" : ""}`}
                ></i>
                <i id="from" className="fas fa-copy"></i>
              </div>
              <select></select>
            </li>
            <li className="exchange">
              <i className="fas fa-exchange-alt"></i>
            </li>
            <li className="row to">
              <select></select>
              <div className="icons">
                <i id="to" className="fas fa-volume-up"></i>
                <i id="to" className="fas fa-copy"></i>
              </div>
            </li>
          </ul>
        </div>
        <button>Translate Text</button>
      </div>
    </>
  );
};

export default Translate;
