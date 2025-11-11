package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.cli.CommandParser;
import com.ZhangRuo.pkm.controller.NoteController;


    public class ViewCommand extends AbstractCommand {

        private final NoteController noteController;

        public ViewCommand(NoteController noteController) {
            super("view", "根据ID查看一篇笔记的详细内容");
            this.noteController = noteController;
        }

        @Override
        public void execute(String[] args) {
            if (args.length != 1) {
                printUsage();
                return;
            }
            // 注意：这里的args[0]现在还是字符串，
            // 短ID到真实ID的翻译工作，将由重构后的CommandParser完成
            noteController.viewNoteById(args[0]);
        }

        @Override
        public void printUsage() {
            System.out.println("用法: view <笔记ID>");
            System.out.println("描述: " + getDescription());
            System.out.println("示例: view 1");
        }
    }