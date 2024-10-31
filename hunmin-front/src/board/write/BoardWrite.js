import React, { Component } from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

class BoardWrite extends Component {
    constructor(props) {
        super(props);
        this.quillRef = React.createRef();
    }

    componentDidMount() {
        this.attachImageHandler();
    }

    attachImageHandler = () => {
        const quillEditor = this.quillRef.getEditor();
        const toolbar = quillEditor.getModule('toolbar');
        toolbar.addHandler('image', this.handleImage);
    };

    handleImage = () => {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = async () => {
            const file = input.files[0];

            // 파일 URL을 생성하여 에디터에 이미지 삽입
            const reader = new FileReader();
            reader.onload = (e) => {
                const imgSrc = e.target.result;
                const editor = this.quillRef.getEditor();
                const range = editor.getSelection(true);

                editor.insertEmbed(range.index, 'image', imgSrc); // 클라이언트에 임시 이미지 삽입
                editor.setSelection(range.index + 1);

                // 이미지 파일을 상태에 저장하여 나중에 서버로 업로드할 수 있게 함
                this.props.setImageUrls((prev) => [...prev, imgSrc]); // setImageFiles 대신 setImageUrls 사용
            };
            reader.readAsDataURL(file); // 파일을 읽어서 Base64 URL로 변환
        };
    };

    modules = {
        toolbar: [
            [{ header: [1, 2, false] }],
            ['bold', 'italic', 'underline', 'strike', 'blockquote'],
            [{ list: 'ordered' }, { list: 'bullet' }, { indent: '-1' }, { indent: '+1' }],
            ['link', 'image'],
            [{ align: [] }, { color: [] }, { background: [] }],
            ['clean'],
        ],
    };

    formats = ['header', 'bold', 'italic', 'underline', 'strike', 'blockquote', 'list', 'bullet', 'indent', 'link', 'image', 'align', 'color', 'background'];

    render() {
        const { value, onChange } = this.props;
        return (
            <div style={{ height: '650px', width: '100%' }}>
                <ReactQuill
                    ref={(el) => {
                        this.quillRef = el;
                    }}
                    style={{ height: '600px' }}
                    theme="snow"
                    modules={this.modules}
                    formats={this.formats}
                    value={value || ''}
                    onChange={(content, delta, source, editor) => onChange(editor.getHTML())}
                />
                <style>
                    {`
                        .quill-image {
                            max-width: 100%; /* 최대 너비를 100%로 설정하여 컨테이너에 맞게 조정 */
                            height: auto; /* 높이는 자동으로 조정 */
                            resize: both; /* 너비와 높이를 모두 조정 가능하게 설정 */
                            overflow: auto; /* 오버플로우를 자동으로 설정하여 크기 조절 시 스크롤 가능하게 */
                            border: 1px solid #ccc; /* 선택된 이미지에 경계선 추가 */
                            display: block; /* 블록 요소로 설정하여 별도의 줄에 표시 */
                        }
                    `}
                </style>
            </div>
        );
    }
}

export default BoardWrite;
