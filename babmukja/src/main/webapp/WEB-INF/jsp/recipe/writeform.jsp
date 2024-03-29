<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>레시피 작성</title>
    <script src="<c:url value="/resources/js/editor.min.js"/>"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/header@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/link@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/list@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/embed@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/raw@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/simple-image@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/checklist@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/quote@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/image@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/simple-image@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/marker@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/table@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/@editorjs/warning@latest"></script>
    <link rel="stylesheet" href="<c:url value="/resources/css/recipe/recipewriteform.css"/>">
</head>
<body>
    <div id="writeform-header">레시피 작성</div>
    <div id="paragraph">자신만의 레시피를 완성해주세요</div>
    <div id="editorjs">
            <input type="text" id="title" placeholder="제목을 입력해주세요.">
        </div>
    <div id="buttonWrapper">
        <button>저장</button>
    </div>
    <script>
   		let fileList="";
    	const editor = new EditorJS({
            holderId: 'editorjs',

            autofocus: false,
            data: {"time":1557295973064,"blocks":[{"type":"quote","data":{"title":"","message":""}},{"type":"warning","data":{"title":"","message":""}}],"version":"2.13.0"},
            tools: { 
                warning: {
                    class: Warning,
                    inlineToolbar: true,
                    shortcut: 'CMD+SHIFT+W',
                    config: {
                        titlePlaceholder: '주의사항',
                        messagePlaceholder: '내용을 입력해주세요',
                    },
                },
     
                table: {
                    class: Table,
                    inlineToolbar: true,
                    config: {
                        rows: 2,
                        cols: 3,
                    },
                },                       
                quote: {
                    class: Quote,
                    inlineToolbar: true,
                    shortcut: 'CMD+SHIFT+O',
                    config: {
                        quotePlaceholder: '내용을 입력해주세요',
                        captionPlaceholder: '출처',
                    },
                },
//                 image: {
//                     class: ImageTool,
//                     config: {
//                     	uploader:{
//                     		uploadByFile(file){
//                     			alert("asdsa");
//                     			console.dir(file);
//                                 // your own uploading logic here
//                                 return $.ajax.upload(file).then(() => {
//                                   return {
//                                     success: 1,
//                                     file: {
//                                       url: 'https://codex.so/upload/redactor_images/o_80beea670e49f04931ce9e3b2122ac70.jpg',
//                                       // any other image data you want to store, such as width, height, color, extension, etc
//                                     }
//                                   };
//                             	});
//                     		}
//                     	}
// //                         endpoints: {
// //                         byFile: 'http://localhost:8085/babmukja/recipe/write.do', // Your backend file uploader endpoint
// //                         byUrl: ''
// //                         }
//                     }
//                 },
				image: {
                    class: ImageTool,
                    config: {
                        uploader: {
                            uploadByFile(file){

                                function imgUpload() {
                                    console.dir(file);
                                    let fileData = new FormData();
                                    
                                        fileData.append("attach", file);  
                                        
                                    return new Promise(function (resolve, reject) {
										alert("이미지 업로드 중...");
                                        $.ajax({
                                            url: 'upload.do',
                                            type: "post",
                    	   					processData: false,
                                          	contentType: false,
                                            data: fileData,
                                            success: function (response) {
                                                
                                            	if (response) {	
                                            		console.dir(response);
                                            		var obj = JSON.parse(response);
                                                	console.log(obj.path);
                                                	console.log(obj.sysname);
//                                                 	console.log("${pageContext.request.contextPath}/recipe/download.do?path="+obj.path+"&sysname=" + obj.sysname);
                                                	resolve({
			                                            cnt: 1,
			                                            url: "${pageContext.request.contextPath}/recipe/download.do?path="+obj.path+"&sysname=" + obj.sysname
			                                        });
                                                }
                                            },
                                            error: function () {
                                                reject(new Error("imgUpload 실패함"));
                                            }
                                        });
                                    });
                                }

                                return imgUpload().then((result) => {
                                    alert("업로드 완료!");
                                    console.dir(result);
                                    return {
                                        success: result.cnt,
                                        file: {
                                            url: result.url,
                                        }
                                    };
                                });
                            }
                        }
                    }
                },            

                header: {
                    class: Header,
                    inlineToolbar: ['link']
                }, 
                checklist: {
                    class: Checklist,
                    inlineToolbar: true,
                },
                linkTool: {
                    class: LinkTool,
                    config: {
                        endpoint: 'http://127.0.0.1:5500', // Your backend endpoint for url data fetching
                    }
                },
                marker: {
                    class: Marker,
                    shortcut: 'ALT+M'
                },
                list: {
                    class: List,
                    "data":{
                    	"style":"ordered"
                    },
                    inlineToolbar: ['link', 'bold']
                },
                embed: {
                    class: Embed,
                    inlineToolbar: false,
                    config: {
                        services: {
                            youtube: true,
                            coub: true
                        }    
                    }
                }
            }
        });

        let saveBtn = document.querySelector("button");
        saveBtn.addEventListener("click", function () {
        	let recipeTitle = $("#title").val();
            console.dir(editor)
            editor.save().then((outputData)=>{
        
            	let cnt = 0;
            	for(let fileUrl of outputData.blocks){
            		if(fileUrl.type == 'image'){
		            	fileList += (cnt==0)?fileUrl.data.file.url:","+fileUrl.data.file.url;   
		            	cnt++;
            		}
            	}
            	
        
            	let content = JSON.stringify(outputData);
            	let title= $("#title").val();
            	$.ajax({
					type: "post",
	   					url:"write.do",
						data: {content : content,
								 title : title,
							   imgPath: fileList},
						success:function(result){
						}
            	});
                console.log("Article data : ", outputData);
                console.log("title : "+title);
                console.log("content : " + content);
                location.href="<c:url value="/recipe/main.do"/>";
            }).catch((error)=>{
                console.log("Saving failed : ", error);
            });
        });
    </script>
</body>
</html>

